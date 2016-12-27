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
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.RequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
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
  private static final int OUTBOUND_CACHE_MAX_SIZE = 10000;
  private static final float OUTBOUND_CACHE_LOAD_FACTOR = 0.8f;
  private final MBassador <Event> eventBus;
  private final Map <RequestEvent, PlayerPacket> requestEvents = Maps.newHashMap ();
  private final Map <ResponseRequestEvent, PlayerInputRequestEvent> responseRequests = Maps.newHashMap ();
  private final Map <InformRequestEvent, PlayerInformEvent> informRequests = Maps.newHashMap ();
  private final Deque <ServerEvent> outboundEventCache = Queues.newArrayDeque ();

  public InternalCommunicationHandler (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  public boolean isSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

    final Optional <PlayerPacket> sender = senderOf (event);
    return sender.isPresent () && sender.get ().is (player);
  }

  public boolean isNotSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

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
    Arguments.checkIsNotNull (type, "type");

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

    outboundEventCache.offer (event);

    if (outboundEventCache.size () < OUTBOUND_CACHE_MAX_SIZE) return;

    final int currentCacheSize = outboundEventCache.size ();
    final int targetCacheSize = (int) (OUTBOUND_CACHE_MAX_SIZE * OUTBOUND_CACHE_LOAD_FACTOR);
    while (outboundEventCache.size () > targetCacheSize)
    {
      final ServerEvent discarded = outboundEventCache.poll ();
      log.trace ("Discarding old event from server revent cache [{}]", discarded);
    }

    log.debug ("Pruned outbound event cache [New Size: {}]; Discarded {} old events.", outboundEventCache.size (),
               currentCacheSize - targetCacheSize);
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

    log.trace ("Event received [{}]", event);

    requestEvents.put (event.getRequestEvent (), event.getPlayer ());
    eventBus.publish (event.getRequestEvent ());
  }
}
