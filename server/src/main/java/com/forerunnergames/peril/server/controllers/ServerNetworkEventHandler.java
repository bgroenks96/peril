package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.core.shared.net.NetworkEventHandler;
import com.forerunnergames.peril.core.shared.net.events.client.request.CreateGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public class ServerNetworkEventHandler extends NetworkEventHandler
{
  private final MultiplayerController controller;

  public ServerNetworkEventHandler (final MultiplayerController controller)
  {
    Arguments.checkIsNotNull (controller, "controller");

    this.controller = controller;

    initialize ();
  }

  @Override
  protected void subscribe (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    eventBus.subscribe (this);
  }

  @Handler
  public void onEvent (final CreateGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.onEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.onEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.onEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.onEvent (event, clientFor (event));
  }
}
