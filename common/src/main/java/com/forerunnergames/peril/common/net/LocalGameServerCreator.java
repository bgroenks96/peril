/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.common.net.events.ipc.ServerInitializationFinishedEvent;
import com.forerunnergames.peril.common.net.events.ipc.ShutDownServerRequestEvent;
import com.forerunnergames.peril.common.net.events.ipc.ShutDownServerResponseEvent;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.ClientInterProcessEvent;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.InterProcessEvent;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.ServerInterProcessEvent;
import com.forerunnergames.peril.common.net.kryonet.KryonetServer;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalGameServerCreator implements GameServerCreator
{
  private static final Logger log = LoggerFactory.getLogger (LocalGameServerCreator.class);
  private final MBassador <InterProcessEvent> ipcEventBus = new MBassador <> ();
  private final ClientInterProcessServer ipcServer = new ClientInterProcessServer (ipcEventBus);
  private final Exchanger <ServerInterProcessEvent> serverCallbackExchange = new Exchanger <> ();
  @Nullable
  private Process serverProcess = null;
  private boolean isCreated = false;

  @Override
  public Result <String> create (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    if (isCreated)
    {
      log.warn ("Cannot launch local server because it is already running.");

      return Result.failure ("The server is already running.");
    }

    log.info ("Launching your local host & play server \"{}\" on port {} (TCP)...", config.getGameServerName (),
              config.getPort ());

    ipcEventBus.subscribe (this);

    final StdoutMonitor monitor = new StdoutMonitor ();

    try
    {
      ipcServer.initialize (config.getCallbackPort ());

      // @formatter:off
      serverProcess = new ProcessBuilder ("java",
                      "-jar",
                      "-ea", // TODO Production: Remove
                      NetworkSettings.SERVER_JAR_NAME, // TODO Specify the server jar name on the command line?
                      "--game-mode", config.getGameMode ().name(),
                      "--server-type", config.getGameServerType ().name (),
                      "--title", config.getGameServerName (),
                      "--port", String.valueOf (config.getPort ()),
                      "--human-players", String.valueOf (config.getPlayerLimitFor (PersonSentience.HUMAN)),
                      "--ai-players", String.valueOf (config.getPlayerLimitFor (PersonSentience.AI)),
                      "--spectators", String.valueOf (config.getSpectatorLimit ()),
                      "--win-percent", String.valueOf (config.getWinPercentage ()),
                      "--assignment", config.getInitialCountryAssignment ().name(),
                      "--map-name", config.getPlayMapName (),
                      "--callback-port", String.valueOf(config.getCallbackPort ()))
                      .redirectErrorStream (true)
                      .start ();
      // @formatter:on

      monitor.readFrom (serverProcess);

      addShutDownHook ();

      final ServerInitializationFinishedEvent initializationEvent;
      initializationEvent = waitForCallback (ServerInitializationFinishedEvent.class, 5, TimeUnit.SECONDS);

      isCreated = true;

      log.info ("Successfully launched your local host & play server \"{}\" on port {} (TCP) [Initialization Time: {} ms]",
                config.getGameServerName (), config.getPort (), initializationEvent.getInitializationTimeMillis ());

      return Result.success ();
    }
    catch (final IOException | InterruptedException | TimeoutException e)
    {
      destroyServerProcess ();

      log.warn ("Failed to launch local server on port [{}] (TCP).", config.getPort ());
      log.warn ("Failure reason: [{}]", Strings.toString (e));
      log.warn ("Server process output: {}", monitor.getStdout ());

      return Result.failure (Strings.toString (e));
    }
  }

  @Override
  public void destroy ()
  {
    if (!isCreated) return;

    log.info ("Destroying your local host & play server...");

    destroyServerProcess ();

    isCreated = false;
  }

  @Override
  public boolean isCreated ()
  {
    return isCreated;
  }

  @Handler
  void handle (final ServerInitializationFinishedEvent event)
  {
    try
    {
      serverCallbackExchange.exchange (event);
    }
    catch (final InterruptedException e)
    {
      log.error ("Error while handling initialization event.", e);
    }
  }

  @Handler
  void handle (final ServerInterProcessEvent event)
  {
    log.debug ("Received IPC message: [{}]", event);
  }

  @Handler
  void handle (final ClientInterProcessEvent event)
  {
    ipcServer.send (event);
  }

  private <T extends ServerInterProcessEvent> T waitForCallback (final Class <T> type,
                                                                 final long timeout,
                                                                 final TimeUnit timeUnit)
          throws InterruptedException, TimeoutException
  {
    final ServerInterProcessEvent received = serverCallbackExchange.exchange (null, timeout, timeUnit);
    if (!type.isInstance (received))
    {
      Exceptions.throwIllegalState ("Expected type [{}] received type [{}]", type, received.getClass ());
    }

    return type.cast (received);
  }

  private void addShutDownHook ()
  {
    Runtime.getRuntime ().addShutdownHook (new Thread (new Runnable ()
    {
      @Override
      public void run ()
      {
        destroyServerProcess ();
      }
    }));
  }

  private void destroyServerProcess ()
  {
    if (serverProcess == null) return;
    ipcEventBus.publish (new ShutDownServerRequestEvent ());
    try
    {
      waitForCallback (ShutDownServerResponseEvent.class, 1, TimeUnit.SECONDS);
    }
    catch (InterruptedException | TimeoutException e)
    {
      log.warn ("Shut down request timer timed out. Server may not have shut down properly!", e);
    }

    ipcServer.shutDown ();

    // make sure the process dies!
    serverProcess.destroy ();
  }

  private class ClientInterProcessServer extends Listener
  {
    private final KryonetServer server = new KryonetServer ();
    private final MBassador <InterProcessEvent> eventBus;
    private Connection endPointConnection;

    ClientInterProcessServer (final MBassador <InterProcessEvent> eventBus)
    {
      this.eventBus = eventBus;
    }

    @Override
    public void connected (final Connection conn)
    {
      log.debug ("Connected: {}", conn);
      endPointConnection = conn;
    }

    @Override
    public void received (final Connection conn, final Object object)
    {
      log.debug ("Received object [{}] from [{}]", object, conn);
      final Class <ServerInterProcessEvent> expectedType = ServerInterProcessEvent.class;
      if (!expectedType.isInstance (object))
      {
        log.warn (Strings.format ("Received unrecognized message [{}] from [{}]", object, conn));
        return;
      }

      eventBus.publish (expectedType.cast (object));
    }

    void initialize (final int port) throws IOException
    {
      server.addListener (this);
      server.start (port);
    }

    void send (final ClientInterProcessEvent event)
    {
      server.sendToTCP (endPointConnection.getID (), event);
    }

    void shutDown ()
    {
      server.shutDown ();
    }
  }

  private class StdoutMonitor implements Runnable
  {
    private final StringBuffer buffer = new StringBuffer ();
    private Process process;

    private void readFrom (final Process process)
    {
      this.process = process;
      final Thread monitorThread = new Thread (this);
      monitorThread.setName (getClass ().getSimpleName ());
      monitorThread.setDaemon (true);
      monitorThread.start ();
    }

    @Override
    public void run ()
    {
      try (final BufferedReader reader = new BufferedReader (new InputStreamReader (process.getInputStream ())))
      {
        while (serverProcess.isAlive ())
        {
          if (!reader.ready ())
          {
            Thread.yield ();
            continue;
          }

          buffer.append (Strings.format ("{}\n", reader.readLine ()));
        }
      }
      catch (final IOException e)
      {
        log.error ("Stdout monitor encountered an error:", e);
      }
    }

    public String getStdout ()
    {
      return buffer.toString ();
    }
  }
}
