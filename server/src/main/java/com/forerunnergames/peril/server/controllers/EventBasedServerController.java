package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.events.local.ClientCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ClientConnectionEvent;
import com.forerunnergames.tools.net.events.local.ClientDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.QuestionEvent;
import com.forerunnergames.tools.net.server.AbstractServerController;
import com.forerunnergames.tools.net.server.Server;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedServerController extends AbstractServerController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedServerController.class);
  private final MBassador <Event> eventBus;
  private final AsyncExecution mainThreadExecutor;

  public EventBasedServerController (final Server server,
                                     final int serverTcpPort,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization,
                                     final MBassador <Event> eventBus,
                                     final AsyncExecution mainThreadExecutor)
  {
    super (server, serverTcpPort, classesToRegisterForNetworkSerialization);

    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.eventBus = eventBus;
    this.mainThreadExecutor = mainThreadExecutor;
  }

  @Override
  protected void onConnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Client [{}] connected.", client);

        eventBus.publish (new ClientConnectionEvent (client));
      }
    });
  }

  @Override
  protected void onDisconnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Client [{}] disconnected.", client);

        eventBus.publish (new ClientDisconnectionEvent (client));
      }
    });
  }

  @Override
  protected void onCommunication (final Object object, final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.debug ("Received object [{}] from client [{}].", object, client);

        if (!(object instanceof QuestionEvent))
        {
          log.debug ("Ignoring unrecognized object [{}] from client [{}].", object, client);

          return;
        }

        eventBus.publish (new ClientCommunicationEvent ((QuestionEvent) object, client));
      }
    });
  }
}
