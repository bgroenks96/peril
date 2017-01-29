package com.forerunnergames.peril.server.main;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.forerunnergames.peril.common.eventbus.EventBusPipe;
import com.forerunnergames.peril.common.net.events.ipc.ServerInitializationFinishedEvent;
import com.forerunnergames.peril.common.net.events.ipc.ShutDownServerRequestEvent;
import com.forerunnergames.peril.common.net.events.ipc.ShutDownServerResponseEvent;
import com.forerunnergames.peril.common.net.events.ipc.ShutDownServerResponseEvent.ResponseCode;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.ClientInterProcessEvent;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.InterProcessEvent;
import com.forerunnergames.peril.common.net.events.ipc.interfaces.ServerInterProcessEvent;
import com.forerunnergames.peril.common.net.kryonet.KryonetClient;
import com.forerunnergames.peril.server.application.ServerApplication;
import com.forerunnergames.tools.common.Strings;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ServerInterProcessClient extends Listener
{
  private static final Logger log = LoggerFactory.getLogger (ServerInterProcessClient.class);
  private static final int CONNECT_TIMEOUT = 2000;
  private final KryonetClient ipcClient = new KryonetClient ();
  private final MBassador <InterProcessEvent> ipcEventBus = new MBassador <> ();
  private ServerApplication application;
  private long initStartTimeMs, initFinishTimeMs;

  ServerInterProcessClient ()
  {
    ipcEventBus.subscribe (this);
  }

  @Override
  public void received (final Connection conn, final Object event)
  {
    final Class <ClientInterProcessEvent> expectedType = ClientInterProcessEvent.class;
    if (!expectedType.isInstance (event))
    {
      log.warn (Strings.format ("Received unrecognized object [{}] from endpoint [{}]", event, conn));
      return;
    }

    ipcEventBus.publish (expectedType.cast (event));
  }

  void initialize (final int callbackPort)
  {
    final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor ();
    asyncExecutor.execute (new Runnable ()
    {
      @Override
      public void run ()
      {
        try
        {
          ipcClient.start ();
          ipcClient.connect (CONNECT_TIMEOUT, "localhost", callbackPort);
          ipcClient.addListener (ServerInterProcessClient.this);
          initStartTimeMs = System.currentTimeMillis ();
        }
        catch (final IOException e)
        {
          log.warn (Strings.format ("Unable to establish connection to local IPC endpoint on port {}", callbackPort),
                    e);
        }
        finally
        {
          asyncExecutor.shutdown ();
        }
      }
    });
  }

  void finishInitialize (final ServerApplication application)
  {
    this.application = application;
    initFinishTimeMs = System.currentTimeMillis ();
    application.subscribe (this);
    send (new ServerInitializationFinishedEvent (initFinishTimeMs - initStartTimeMs));
  }

  <T> EventBusPipe <InterProcessEvent, T> createPipe ()
  {
    return new EventBusPipe <> (ipcEventBus);
  }

  void send (final InterProcessEvent event)
  {
    if (!ipcClient.isConnected ()) return;
    log.trace ("Sending IPC message: [{}]", event);
    ipcClient.send (event); // for some reason, this is hanging... no idea why
    log.debug ("Send finished!");
  }

  void disconnect ()
  {
    if (!ipcClient.isConnected ()) return;
    ipcClient.disconnect ();
    ipcClient.stop ();
  }

  void subscribe (final Object subscriber)
  {
    ipcEventBus.subscribe (subscriber);
  }

  @Handler
  void handleOutboundIpcEvent (final ServerInterProcessEvent event)
  {
    send (event);
  }

  @Handler
  void handleInboundIpcEvent (final ClientInterProcessEvent event)
  {
    log.trace ("Received IPC message: [{}]", event);
  }

  @Handler
  void handleInboundShutdownRequest (final ShutDownServerRequestEvent event)
  {
    log.info ("Attempting to shut down server application...");

    try
    {
      shutDown ();
    }
    catch (final Exception e)
    {
      log.error ("Failed to shut down server application!", e);
      send (new ShutDownServerResponseEvent (ResponseCode.FAILURE));
      return;
    }

    log.info ("Successfully shut down server application!");
    send (new ShutDownServerResponseEvent (ResponseCode.OK));
    disconnect ();
  }

  private void shutDown ()
  {
    ipcEventBus.shutdown ();
    if (application != null) application.shutDown ();
  }
}
