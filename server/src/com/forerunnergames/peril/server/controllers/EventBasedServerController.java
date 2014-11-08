package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.AbstractServerController;
import com.forerunnergames.tools.common.net.Remote;
import com.forerunnergames.tools.common.net.Server;
import com.forerunnergames.tools.common.net.events.ClientCommunicationEvent;
import com.forerunnergames.tools.common.net.events.ClientConnectionEvent;
import com.forerunnergames.tools.common.net.events.ClientDisconnectionEvent;
import com.forerunnergames.tools.common.net.events.QuestionEvent;

import com.google.common.collect.ImmutableSet;

import org.bushe.swing.event.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedServerController extends AbstractServerController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedServerController.class);

  public EventBasedServerController (final Server server,
                                     final int serverTcpPort,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization)
  {
    super (server, serverTcpPort, classesToRegisterForNetworkSerialization);
  }

  @Override
  protected void onConnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    log.info ("Client [{}] connected.", client);

    EventBus.publish (new ClientConnectionEvent (client));
  }

  @Override
  protected void onDisconnection (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    log.info ("Client [{}] disconnected.", client);

    EventBus.publish (new ClientDisconnectionEvent (client));
  }

  @Override
  protected void onCommunication (final Object object, final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    log.debug ("Received object [{}] from client [{}].", object, client);

    if (! (object instanceof QuestionEvent))
    {
      log.debug ("Ignoring unrecognized object [{}] from client [{}].", object, client);

      return;
    }

    EventBus.publish (new ClientCommunicationEvent ((QuestionEvent) object, client));
  }
}
