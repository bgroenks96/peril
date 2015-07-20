package com.forerunnergames.peril.client.controllers;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.AbstractClientController;
import com.forerunnergames.tools.net.client.Client;
import com.forerunnergames.tools.net.events.local.ServerCommunicationEvent;
import com.forerunnergames.tools.net.events.local.ServerConnectionEvent;
import com.forerunnergames.tools.net.events.local.ServerDisconnectionEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBasedClientController extends AbstractClientController
{
  private static final Logger log = LoggerFactory.getLogger (EventBasedClientController.class);
  private final MBassador <Event> eventBus;
  private final AsyncExecution mainThreadExecutor;

  public EventBasedClientController (final Client client,
                                     final ImmutableSet <Class <?>> classesToRegisterForNetworkSerialization,
                                     final MBassador <Event> eventBus,
                                     final AsyncExecution mainThreadExecutor)
  {
    super (client, classesToRegisterForNetworkSerialization);

    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (mainThreadExecutor, "mainThreadExecutor");

    this.eventBus = eventBus;
    this.mainThreadExecutor = mainThreadExecutor;
  }

  @Override
  protected void onConnectionTo (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Connected to server [{}].", server);

        eventBus.publish (new ServerConnectionEvent (server));
      }
    });
  }

  @Override
  protected void onDisconnectionFrom (final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.info ("Disconnected from server [{}].", server);

        eventBus.publish (new ServerDisconnectionEvent (server));
      }
    });
  }

  @Override
  protected void onCommunication (final Object object, final Remote server)
  {
    Arguments.checkIsNotNull (server, "server");

    mainThreadExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.debug ("Received object [{}] from server [{}].", object, server);

        if (!(object instanceof ServerEvent))
        {
          log.warn ("Ignoring unrecognized object [{}] from server [{}].", object, server);

          return;
        }

        eventBus.publish (new ServerCommunicationEvent ((Event) object, server));
      }
    });
  }
}
