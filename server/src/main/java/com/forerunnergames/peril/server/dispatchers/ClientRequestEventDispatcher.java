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

package com.forerunnergames.peril.server.dispatchers;

import com.forerunnergames.peril.common.net.dispatchers.AbstractNetworkEventDispatcher;
import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.listener.Handler;

/**
 * Uses {@link net.engio.mbassy.bus.MBassador} to resolve the runtime types of various events implementing
 * {@link com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent}, notifying the specified
 * {@link ClientRequestEventDispatchListener} along with the remote client who originally sent the event via
 * {@link #senderOfDispatchedEvent(Event)}.
 */
public final class ClientRequestEventDispatcher extends AbstractNetworkEventDispatcher
{
  private final ClientRequestEventDispatchListener listener;

  public ClientRequestEventDispatcher (final Iterable <IPublicationErrorHandler> internalBusErrorHandlers,
                                       final ClientRequestEventDispatchListener listener)
  {
    super (internalBusErrorHandlers);

    Arguments.checkIsNotNull (listener, "listener");

    this.listener = listener;
  }

  @Handler
  void onEvent (final HumanJoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final AiJoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final SpectatorJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final ChatMessageRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final PlayerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final ResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }

  @Handler
  void onEvent (final InformRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event [{}] received", event);

    listener.handleEvent (event, senderOfDispatchedEvent (event));
  }
}
