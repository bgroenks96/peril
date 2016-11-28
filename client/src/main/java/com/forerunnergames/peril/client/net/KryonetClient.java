/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.common.net.kryonet.KryonetLogging;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.client.remote.RemoteServer;
import com.forerunnergames.tools.net.client.remote.RemoteServerListener;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KryonetClient extends com.esotericsoftware.kryonet.Client implements Client
{
  private static final Logger log = LoggerFactory.getLogger (KryonetClient.class);
  private final Map <Integer, RemoteServer> connectionIdsToRemoteServers = new HashMap<> ();
  private final Map <RemoteServerListener, Listener> remoteServerToKryonetListeners = new HashMap<> ();
  private final ExecutorService executorService = Executors.newSingleThreadExecutor ();
  private final Kryo kryo;
  private boolean isRunning = false;

  public KryonetClient ()
  {
    super (NetworkSettings.CLIENT_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES,
           NetworkSettings.CLIENT_SERIALIZATION_READ_BUFFER_SIZE_BYTES);

    kryo = getKryo ();

    KryonetLogging.initialize ();
    KryonetRegistration.initialize (kryo);
    KryonetRegistration.registerCustomSerializers (kryo);
  }

  @Override
  public void start ()
  {
    super.start ();

    isRunning = true;

    log.info ("Started the client");
  }

  @Override
  public void stop ()
  {
    if (!isRunning) return;

    super.stop ();

    isRunning = false;

    log.info ("Stopped the client");
  }

  @Override
  public void send (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    if (!isRunning)
    {
      log.warn ("Prevented sending object [{}] to the server because the client hasn't been started.", object);

      return;
    }

    if (!isConnected ())
    {
      log.warn ("Prevented sending object [{}] to the server because the client is disconnected.", object);

      return;
    }

    sendTCP (object);

    log.debug ("Sent object [{}] to the server", object);
  }

  @Override
  public void add (final RemoteServerListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final Listener kryonetListener = new Listener ()
    {
      @Override
      public void connected (final Connection connection)
      {
        Arguments.checkIsNotNull (connection, "connection");

        RemoteServer server = connectionIdsToRemoteServers.get (connection.getID ());

        if (server != null)
        {
          log.error ("Connected to duplicate server [{}]! Connection: [{}]. Not notifying listener: [{}].", server,
                     connection, listener);
          return;
        }

        server = new KryonetRemoteServer (connection.getID (), connection.getRemoteAddressTCP ());
        connectionIdsToRemoteServers.put (connection.getID (), server);

        listener.connected (server);
      }

      @Override
      public void disconnected (final Connection connection)
      {
        Arguments.checkIsNotNull (connection, "connection");

        final RemoteServer server = connectionIdsToRemoteServers.get (connection.getID ());

        if (server == null)
        {
          log.error ("Disconnected from non-connected server! Connection: [{}]. Not notifying listener: [{}].",
                     connection, listener);
          return;
        }

        connectionIdsToRemoteServers.remove (connection.getID ());

        listener.disconnected (server);
      }

      @Override
      public void received (final Connection connection, @Nullable final Object object)
      {
        Arguments.checkIsNotNull (connection, "connection");

        final RemoteServer server = connectionIdsToRemoteServers.get (connection.getID ());

        if (server == null)
        {
          log.error ("Received object [{}] from non-connected server! Connection: [{}]. Not notifying listener: [{}].",
                     object, connection, listener);
          return;
        }

        if (object instanceof FrameworkMessage) return;

        if (object == null)
        {
          log.warn ("Received null object from server: [{}].", connection.getRemoteAddressTCP ());
          return;
        }

        listener.received (server, object);
      }
    };

    addListener (kryonetListener);

    remoteServerToKryonetListeners.put (listener, kryonetListener);
  }

  @Override
  public void remove (final RemoteServerListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    removeListener (remoteServerToKryonetListeners.remove (listener));
  }

  @Override
  public void register (final Class <?> type)
  {
    Arguments.checkIsNotNull (type, "type");

    kryo.register (type);

    log.trace ("Registered class [{}] with the server for network serialization.", type);
  }

  @Override
  public void shutDown ()
  {
    disconnect ();
    stop ();
  }

  @Override
  public Result <String> connectNow (final ServerConfiguration config, final int timeoutMs, final int maxAttempts)
  {
    Arguments.checkIsNotNull (config, "config");
    Arguments.checkIsNotNegative (timeoutMs, "timeoutMs");
    Arguments.checkLowerInclusiveBound (maxAttempts, 0, "maxAttempts");

    if (isConnected ())
    {
      log.warn ("Cannot connect to the server because you are already connected.");
      return Result.failure ("You are already connected to the server.");
    }

    log.info ("Connecting to server at address [{}] & port [{}] (TCP)...", config.getAddress (), config.getPort ());

    int connectionAttempts = 0;
    Result <String> result;

    do
    {
      ++connectionAttempts;

      log.info ("[{}] connection attempt...", Strings.toMixedOrdinal (connectionAttempts));

      result = connectNow (config.getAddress (), config.getPort (), timeoutMs);
    }
    while (result.failed () && connectionAttempts < maxAttempts);

    return result;
  }

  @Override
  public Future <Result <String>> connectLater (final ServerConfiguration config,
                                                final int timeoutMs,
                                                final int maxAttempts)
  {
    return executorService.submit (new Callable <Result <String>> ()
    {
      @Override
      public Result <String> call ()
      {
        return connectNow (config, timeoutMs, maxAttempts);
      }
    });
  }

  @Override
  public void disconnect ()
  {
    if (!isConnected ()) return;

    close ();
  }

  private Result <String> connectNow (final String address, final int port, final int timeoutMs)
  {
    log.info ("Attempting to connect to server with address [{}] on port [{}] (TCP).", address, port);

    try
    {
      connect (timeoutMs, address, port);

      // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <String> type.
      return isConnected () ? Result.<String> success () : Result.failure ("Unknown");
    }
    catch (final IOException e)
    {
      log.info ("Failed to connect to server with address [{}] on port [{}] (TCP).", address, port);
      log.debug ("Failure reason: [{}]", Strings.toString (e));

      return Result.failure ("Could not connect to server with address [" + address + "] on port [" + port
              + "] (TCP). Details:\n\n" + Strings.toString (e));
    }
  }
}
