package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.core.shared.net.NetworkEventHandler;
import com.forerunnergames.peril.core.shared.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.listener.Handler;

public class ServerNetworkEventHandler extends NetworkEventHandler
{
  private final MultiplayerController controller;

  /**
   * @param controller
   *          MultiplayerController to which registered network events will be dispatched
   * @param internalBusErrorHandlers
   *          any error handlers that should be registered with the NetworkEventHandler internal event bus.
   */
  public ServerNetworkEventHandler (final MultiplayerController controller,
                                    final Iterable <IPublicationErrorHandler> internalBusErrorHandlers)
  {
    super (internalBusErrorHandlers);

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
  public void onEvent (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final ChatMessageRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }
}
