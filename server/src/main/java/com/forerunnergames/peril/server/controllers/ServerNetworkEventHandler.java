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

package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.NetworkEventHandler;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

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
  public void onEvent (final HumanJoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final AiJoinGameServerRequestEvent event)
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
  public void onEvent (final SpectatorJoinGameRequestEvent event)
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
  public void onEvent (final PlayerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }

  @Handler
  public void onEvent (final ResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    controller.handleEvent (event, clientFor (event));
  }
}
