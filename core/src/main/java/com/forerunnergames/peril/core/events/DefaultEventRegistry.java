package com.forerunnergames.peril.core.events;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;

import java.util.Deque;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEventRegistry implements EventRegistry
{
  private static final Logger log = LoggerFactory.getLogger (DefaultEventRegistry.class);
  private static final int OUTBOUND_CACHE_MAX_SIZE = 10000;
  private static final float OUTBOUND_CACHE_LOAD_FACTOR = 0.8f;
  private final Deque <ServerEvent> outboundEventCache = Queues.newArrayDeque ();
  private final Map <Event, PlayerPacket> eventsToPlayers = Maps.newConcurrentMap ();
  private final Multimap <PlayerPacket, Event> playersToEvents = HashMultimap.create ();
  private final MBassador <Event> eventBus = new MBassador <> ();

  public DefaultEventRegistry (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");
  }

  @Override
  public void initialize ()
  {
    eventBus.subscribe (this);
  }

  @Override
  public void shutDown ()
  {
    eventBus.unsubscribe (this);
    clearRegistry ();
    clearCache ();
  }

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

  @Override
  public void clearRegistry ()
  {
    eventsToPlayers.clear ();
    playersToEvents.clear ();
  }

  @Override
  public void clearCache ()
  {
    outboundEventCache.clear ();
  }

  // ----- Event Handlers ----- //

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
      log.trace ("Discarding old event from server event cache [{}]", discarded);
    }

    log.debug ("Pruned outbound event cache [New Size: {}]; Discarded {} old events.", outboundEventCache.size (),
               currentCacheSize - targetCacheSize);
  }

  @Handler
  void onEvent (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    registerTo (event.getPerson (), event);
  }
}
