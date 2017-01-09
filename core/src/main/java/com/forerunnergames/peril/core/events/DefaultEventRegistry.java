/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.events;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerAnswerEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerInputCanceledEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import de.matthiasmann.AsyncExecution;

import java.util.Deque;
import java.util.Map;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventRegistry implements EventRegistry
{
  private static final Logger log = LoggerFactory.getLogger (DefaultEventRegistry.class);
  private static final int OUTBOUND_CACHE_MAX_SIZE = 10000;
  private static final int INBOUND_CACHE_MAX_SIZE = 10000;
  private static final float OUTBOUND_CACHE_LOAD_FACTOR = 0.5f;
  private static final float INBOUND_CACHE_LOAD_FACTOR = 0.5f;
  private final Deque <Event> outboundEventCache = Queues.newArrayDeque ();
  private final Deque <Event> inboundEventCache = Queues.newArrayDeque ();
  private final Map <Event, PlayerPacket> eventsToPlayers = Maps.newConcurrentMap ();
  private final Multimap <PlayerPacket, Event> playersToEvents = HashMultimap.create ();
  private final Set <PlayerInputEvent> unmappedInputEvents = Sets.newHashSet ();
  private final BiMap <PlayerAnswerEvent <?>, PlayerInputEvent> answerToInputEvents;
  private final MBassador <Event> eventBus;
  private final AsyncExecution asyncExecutor;

  public DefaultEventRegistry (final MBassador <Event> eventBus, final AsyncExecution asyncExecutor)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
    this.asyncExecutor = asyncExecutor;

    answerToInputEvents = HashBiMap.create ();

    eventBus.subscribe (this);
  }

  // ------- EventRegistry Implementation ------- //

  @Override
  public void registerTo (final PlayerPacket player, final Event event)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (event, "event");

    playersToEvents.put (player, event);
    eventsToPlayers.put (event, player);
  }

  @Override
  public Optional <PlayerPacket> playerFor (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (eventsToPlayers.get (event));
  }

  @Override
  public ImmutableSet <Event> eventsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return ImmutableSet.copyOf (playersToEvents.get (player));
  }

  @Override
  public boolean isSenderOf (final Event event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

    final Optional <PlayerPacket> sender = senderOf (event);
    return sender.isPresent () && sender.get ().is (player);
  }

  @Override
  public boolean isNotSenderOf (final Event event, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (player, "player");

    return !isSenderOf (event, player);
  }

  @Override
  public Optional <PlayerPacket> senderOf (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    return Optional.fromNullable (eventsToPlayers.get (event));
  }

  @Override
  public <T extends PlayerInputEvent> Optional <T> inputEventFor (final PlayerAnswerEvent <T> event,
                                                                  final Class <T> inputRequestType)
  {
    Arguments.checkIsNotNull (inputRequestType, "inputRequestType");
    Arguments.checkIsNotNull (event, "event");

    final PlayerInputRequestEvent inputRequestEvent = (PlayerInputRequestEvent) answerToInputEvents.get (event);
    return Optional.fromNullable (inputRequestType.cast (inputRequestEvent));
  }

  @Override
  public <T extends ServerEvent> Optional <T> lastOutboundEventOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return lastEventOfType (outboundEventCache, type);
  }

  @Override
  public <T extends ClientEvent> Optional <T> lastInboundEventOfType (final Class <T> type)
  {
    Arguments.checkIsNotNull (type, "type");

    return lastEventOfType (inboundEventCache, type);
  }

  @Override
  public void clearRegistry ()
  {
    eventsToPlayers.clear ();
    playersToEvents.clear ();
    cancelAndDiscardInputEventCache ();
  }

  @Override
  public void clearOutboundCache ()
  {
    outboundEventCache.clear ();
  }

  @Override
  public void shutDown ()
  {
    eventBus.unsubscribe (this);
    clearRegistry ();
    clearOutboundCache ();
  }

  // ------- Outbound Event Handlers ------- //

  @Handler
  void onEvent (final ServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    outboundEventCache.offer (event);

    if (outboundEventCache.size () < OUTBOUND_CACHE_MAX_SIZE) return;

    final int discardCount = prune (outboundEventCache, OUTBOUND_CACHE_MAX_SIZE, OUTBOUND_CACHE_LOAD_FACTOR);
    log.debug ("Pruned outbound event cache [New Size: {}]; Discarded {} old events.", outboundEventCache.size (),
               discardCount);
  }

  @Handler
  void onEvent (final PlayerInputEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    unmappedInputEvents.add (event);
  }

  @Handler
  void onEvent (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    registerTo (event.getPerson (), event);
  }

  @Handler
  void onEvent (final PlayerDeniedEvent <?, ?> event)
  {
    Arguments.checkIsNotNull (event, "event");

    final PlayerRequestEvent deniedRequest = event.getDeniedRequest ();

    // Do not republish if denied request event is not an answer
    if (!(deniedRequest instanceof PlayerAnswerEvent)) return;

    republishFor ((PlayerAnswerEvent <?>) deniedRequest);
  }

  // ------- Inbound Event Handlers ------- //

  // This handler needs to execute first in order to ensure that the event mapping is present for other handler code in
  // Core that might need it.
  @Handler (priority = Integer.MAX_VALUE)
  <T extends PlayerInputEvent> void onEvent (final PlayerAnswerEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final ImmutableSet <T> inputEventMatches = allUnmappedInputEventsOfType (event.getQuestionType ());
    if (inputEventMatches.isEmpty ())
    {
      log.warn ("Received answer event with no corresponding outbound event! [{}]", event);
      return;
    }

    final Optional <PlayerPacket> playerMaybe = playerFor (event);
    if (!playerMaybe.isPresent ())
    {
      log.warn ("Received answer event with no player mapping! [{}]", event);
      return;
    }

    final PlayerPacket player = playerMaybe.get ();
    for (final T inputEvent : inputEventMatches)
    {
      if (inputEvent.getPerson ().isNot (player))
      {
        continue;
      }

      map (event, inputEvent);
    }
  }

  void onEvent (final ClientEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    inboundEventCache.offer (event);

    if (inboundEventCache.size () < INBOUND_CACHE_MAX_SIZE) return;

    final int discardCount = prune (inboundEventCache, INBOUND_CACHE_MAX_SIZE, INBOUND_CACHE_LOAD_FACTOR);
    log.debug ("Pruned inbound event cache [New Size: {}]; Discarded {} old events.", outboundEventCache.size (),
               discardCount);
  }

  // ------- Private Utility Methods ------- //

  private <T extends PlayerInputEvent> boolean republishFor (final PlayerAnswerEvent <T> answerEvent)
  {
    final PlayerInputEvent inputEvent = answerToInputEvents.get (answerEvent);
    if (inputEvent == null)
    {
      log.warn ("No event found to republish for [{}]", answerEvent);
      return false;
    }

    asyncExecutor.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        eventBus.publish (inputEvent);
      }
    });
    return true;
  }

  private <T extends PlayerInputEvent> ImmutableSet <T> allUnmappedInputEventsOfType (final Class <T> inputEventType)
  {
    return ImmutableSet.copyOf (Iterables.filter (unmappedInputEvents, inputEventType));
  }

  private void map (final PlayerAnswerEvent <?> answerEvent, final PlayerInputEvent inputEvent)
  {
    final PlayerInputEvent previous = answerToInputEvents.forcePut (answerEvent, inputEvent);
    if (previous != null)
    {
      log.warn ("Overwrote previous mapping for [{}] => [{}], replaced with [{}]", answerEvent, previous, inputEvent);
    }

    unmappedInputEvents.remove (inputEvent);
  }

  private void cancelAndDiscardInputEventCache ()
  {
    for (final PlayerInputEvent next : unmappedInputEvents)
    {
      log.debug ("Publishing cancellation for unmapped input event [{}]", next);
      eventBus.publish (new PlayerInputCanceledEvent (next));
    }

    unmappedInputEvents.clear ();
    answerToInputEvents.clear ();
  }

  private static <T extends Event> Optional <T> lastEventOfType (final Deque <Event> eventCache, final Class <T> type)
  {
    final Deque <Event> tempDeque = Queues.newArrayDeque ();
    Optional <T> maybe = Optional.absent ();
    while (!maybe.isPresent () && !eventCache.isEmpty ())
    {
      final Event next = eventCache.poll ();
      tempDeque.push (next);
      if (next.getClass ().equals (type)) maybe = Optional.of (type.cast (next));
    }

    // push events back into cache in the same order they were removed
    for (final Event next : tempDeque)
    {
      eventCache.push (next);
    }

    return maybe;
  }

  /*
   * Discards old events in the given event Deque collection using the given max size and load factor.
   */
  private static int prune (final Deque <Event> eventCache, final int maxSize, final float loadFactor)
  {
    final int currentCacheSize = eventCache.size ();
    final int targetCacheSize = (int) (maxSize * loadFactor);
    while (eventCache.size () > targetCacheSize)
    {
      final Event discarded = eventCache.poll ();
      log.trace ("Discarding old event from server event cache [{}]", discarded);
    }

    return currentCacheSize - eventCache.size ();
  }
}
