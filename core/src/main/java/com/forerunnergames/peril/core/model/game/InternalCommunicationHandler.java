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

package com.forerunnergames.peril.core.model.game;

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.player.InboundPlayerInformRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.InboundPlayerRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.InboundPlayerResponseRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.PlayerDisconnectedEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataResponseEvent;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.state.events.SuspendGameEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.RequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import java.util.Deque;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Handler class for internal communication events from server
public class InternalCommunicationHandler
{
  private static final Logger log = LoggerFactory.getLogger (InternalCommunicationHandler.class);
  private final PlayerModel playerModel;
  private final MBassador <Event> eventBus;
  private final Map <RequestEvent, PlayerPacket> requestEvents = Maps.newHashMap ();
  private final Map <ResponseRequestEvent, PlayerInputRequestEvent> responseRequests = Maps.newHashMap ();
  private final Map <InformRequestEvent, PlayerInformEvent> informRequests = Maps.newHashMap ();
  private final Deque <ServerEvent> outboundEventCache = Queues.newArrayDeque ();

  public InternalCommunicationHandler (final PlayerModel playerModel, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.eventBus = eventBus;
  }

  public boolean isSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    final Optional <PlayerPacket> sender = senderOf (event);
    return sender.isPresent () && sender.get ().is (player);
  }

  public boolean isNotSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    return !isSenderOf (event, player);
  }

  /**
   * Fetches the PlayerPacket representing the player from whom this client request event was received.
   */
  public Optional <PlayerPacket> senderOf (final RequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (requestEvents.get (event));
  }

  /**
   * Fetches the {@link PlayerInputRequestEvent} corresponding to this ResponseRequestEvent.
   */
  public Optional <PlayerInputRequestEvent> inputRequestFor (final ResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (responseRequests.get (event));
  }

  /**
   * Fetches the {@link PlayerInformEvent} corresponding to this {@link InformRequestEvent}.
   */
  public Optional <PlayerInformEvent> informEventFor (final InformRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (informRequests.get (event));
  }

  public <T extends ServerEvent> Optional <T> lastOutboundEventOfType (final Class <T> type)
  {
    final Deque <ServerEvent> tempDeque = Queues.newArrayDeque ();
    Optional <T> maybe = Optional.absent ();
    while (!maybe.isPresent () && !outboundEventCache.isEmpty ())
    {
      final ServerEvent next = outboundEventCache.poll ();
      tempDeque.push (next);
      if (next.getClass ().equals (type)) maybe = Optional.of (type.cast (next));
    }

    // push events back into cache in the same order they were removed
    for (final ServerEvent next : tempDeque)
    {
      outboundEventCache.push (next);
    }

    return maybe;
  }

  /**
   * This method should be called periodically to avoid stale request events from polluting the map caches.
   */
  public void clearEventCache ()
  {
    log.debug ("Clearing internal event caches [{} RequestEvents] [{} ResponseRequestEvents].", requestEvents.size (),
               responseRequests.size ());

    requestEvents.clear ();
    responseRequests.clear ();
    informRequests.clear ();
  }

  @Handler
  void onEvent (final ServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    outboundEventCache.push (event);
  }

  @Handler (priority = 1)
  void onEvent (final InboundPlayerResponseRequestEvent <? extends ResponseRequestEvent, ? extends PlayerInputRequestEvent> event)
  {
    Arguments.checkIsNotNull (event, "event");

    responseRequests.put (event.getRequestEvent (), event.getOriginalRequestEvent ());
  }

  @Handler (priority = 1)
  void onEvent (final InboundPlayerInformRequestEvent <? extends InformRequestEvent, ? extends PlayerInformEvent> event)
  {
    Arguments.checkIsNotNull (event, "event");

    informRequests.put (event.getRequestEvent (), event.getOriginalInformEvent ());
  }

  @Handler (priority = 0)
  void onEvent (final InboundPlayerRequestEvent <? extends RequestEvent> event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    requestEvents.put (event.getRequestEvent (), event.getPlayer ());
    eventBus.publish (event.getRequestEvent ());
  }

  @Handler
  void onEvent (final UpdatePlayerDataRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    final ImmutableSet <PlayerPacket> players = playerModel.getPlayerPackets ();
    eventBus.publish (new UpdatePlayerDataResponseEvent (players, event.getEventId ()));
  }

  @Handler
  void onEvent (final PlayerDisconnectedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    if (!playerModel.existsPlayerWith (event.getPlayerName ()))
    {
      return;
    }

    log.debug ("Player [{}] disconnected unexpectedly!", event.getPlayer ());
    eventBus.publish (new SuspendGameEvent ());
  }
}
