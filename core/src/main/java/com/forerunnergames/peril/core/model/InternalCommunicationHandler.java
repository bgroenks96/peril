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

package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerResponseTimeoutEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.events.internal.player.InboundPlayerRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.InboundPlayerResponseRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataRequestEvent;
import com.forerunnergames.peril.core.events.internal.player.UpdatePlayerDataResponseEvent;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.RequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Handler class for internal communication events from server
class InternalCommunicationHandler
{
  private static final Logger log = LoggerFactory.getLogger (InternalCommunicationHandler.class);
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final PlayerTurnModel playerTurnModel;
  private final MBassador <Event> eventBus;
  private final Set <EventListener> eventTimeoutListeners = Sets.newConcurrentHashSet ();
  private final Map <RequestEvent, PlayerPacket> requestEvents = Maps.newHashMap ();
  private final Map <ResponseRequestEvent, PlayerInputRequestEvent> responseRequests = Maps.newHashMap ();
  private final Deque <ServerEvent> outboundEventCache = Queues.newArrayDeque ();

  InternalCommunicationHandler (final PlayerModel playerModel,
                                final PlayMapModel playMapModel,
                                final PlayerTurnModel playerTurnModel,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.playerTurnModel = playerTurnModel;
    this.eventBus = eventBus;
  }

  boolean isSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    final Optional <PlayerPacket> sender = senderOf (event);
    return sender.isPresent () && sender.get ().is (player);
  }

  boolean isNotSenderOf (final RequestEvent event, final PlayerPacket player)
  {
    return !isSenderOf (event, player);
  }

  /**
   * Fetches the PlayerPacket representing the player from whom this client request event was received.
   */
  Optional <PlayerPacket> senderOf (final RequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (requestEvents.get (event));
  }

  /**
   * Fetches the PlayerInputRequestEvent corresponding to this ResponseRequestEvent.
   */
  Optional <PlayerInputRequestEvent> requestFor (final ResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (responseRequests.get (event));
  }

  <T extends ServerEvent> Optional <T> lastOutboundEventOfType (final Class <T> type)
  {
    final Deque <ServerEvent> tempDeque = Queues.newArrayDeque ();
    Optional <T> maybe = Optional.absent ();
    while (!maybe.isPresent () && outboundEventCache.size () > 0)
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
  void clearEventCache ()
  {
    log.debug ("Clearing internal event caches [{} RequestEvents] [{} ResponseRequestEvents].", requestEvents.size (),
               responseRequests.size ());

    this.requestEvents.clear ();
    this.responseRequests.clear ();
  }

  void startTimerFor (final Class <? extends ClientRequestEvent> eventType,
                      final PlayerPacket sender,
                      final long timeout,
                      final TimeUnit timeUnit)
  {
    final EventListener listener = new EventListener (eventType, sender);
    if (eventTimeoutListeners.contains (listener))
    {
      Exceptions.throwIllegalState ("Cannot register duplicate event timer for [Type: {} Sender: [{}]].", eventType,
                                    sender);
    }

    final Timer timer = new Timer ();
    timer.schedule (new TimerTask ()
    {
      @Override
      public void run ()
      {
        if (!eventTimeoutListeners.contains (listener))
        {
          return;
        }

        eventTimeoutListeners.remove (listener);

        eventBus.publish (new PlayerResponseTimeoutEvent (sender, eventType.getSimpleName (), timeout, timeUnit));
      }
    }, timeUnit.toMillis (timeout));
  }

  @Handler (priority = Integer.MAX_VALUE)
  void onEvent (final ClientRequestEvent event)
  {
    // efficiency guard clause
    if (eventTimeoutListeners.isEmpty ()) return;

    Optional <EventListener> toRemove = Optional.absent ();
    for (final EventListener listener : eventTimeoutListeners)
    {
      final Optional <PlayerPacket> sender = senderOf (event);
      if (!sender.isPresent ()) continue;
      if (sender.get ().isNot (listener.sender)) continue;
      if (!listener.type.equals (event.getClass ())) continue;

      toRemove = Optional.of (listener);
      break;
    }

    if (toRemove.isPresent ()) eventTimeoutListeners.remove (toRemove.get ());
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
  void onEvent (final InternalPlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    if (!playerModel.existsPlayerWith (event.getPlayerName ())) return;

    final Id player = playerModel.idOf (event.getPlayerName ());
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();

    countryOwnerModel.unassignAllCountriesOwnedBy (player);
    playerModel.remove (player);
    playerTurnModel.setTurnCount (playerModel.getPlayerLimit ());

    eventBus.publish (new PlayerLeaveGameEvent (event.getPlayer (), playerModel.getPlayerPackets ()));
  }

  private class EventListener
  {
    final Class <? extends ClientRequestEvent> type;
    final PlayerPacket sender;

    EventListener (final Class <? extends ClientRequestEvent> type, final PlayerPacket sender)
    {
      this.type = type;
      this.sender = sender;
    }

    @Override
    public int hashCode ()
    {
      return type.hashCode () ^ sender.hashCode ();
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof EventListener)) return false;
      final EventListener event = (EventListener) obj;
      return event.type.equals (this.type) && event.sender.is (this.sender);
    }
  }
}
