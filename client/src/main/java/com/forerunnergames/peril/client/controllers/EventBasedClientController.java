package com.forerunnergames.peril.client.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.net.AbstractClientController;
import com.forerunnergames.tools.common.net.Client;
import com.forerunnergames.tools.common.net.Remote;
import com.forerunnergames.tools.common.net.events.AnswerEvent;
import com.forerunnergames.tools.common.net.events.ServerCommunicationEvent;
import com.forerunnergames.tools.common.net.events.ServerConnectionEvent;
import com.forerunnergames.tools.common.net.events.ServerDisconnectionEvent;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedClientController extends AbstractClientController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedClientController.class);
  private final MBassador <Event> eventBus;

  public EventBasedClientController (final Client client,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization,
                                     final MBassador <Event> eventBus)
  {
    super (client, classesToRegisterForNetworkSerialization);

    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  @Override
  protected void onConnectionTo (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    log.info ("Connected to server [{}].", server);

    eventBus.publish (new ServerConnectionEvent (server));
  }

  @Override
  protected void onDisconnectionFrom (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    log.info ("Disconnected from server [{}].", server);

    eventBus.publish (new ServerDisconnectionEvent (server));
  }

  @Override
  protected void onCommunication (final Object object, final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    log.debug ("Received object [{}] from server [{}].", object, server);

    if (! (object instanceof AnswerEvent))
    {
      log.warn ("Received unrecognized object [{}] from server [{}].", object, server);

      return;
    }

    eventBus.publish (new ServerCommunicationEvent ((AnswerEvent) object, server));
  }
}
