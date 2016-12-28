package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Internal MultiplayerController utility type that consolidates and manages server's input/pending event caches, as
 * well as provides a timer mechanism for events sitting in the cache.
 */
final class MultiplayerControllerEventCache
{
  private static final String TIMER_NAME = "PlayerInputEvent_Timer";
  private final PlayerInputEventTimeoutCallback timeoutCallback;
  private final long inputEventTimeoutMillis;

  private final Timer inputEventTimer;
  private final Multimap <PlayerPacket, PlayerInputRequestEvent> playerInputRequestEventCache;
  private final Multimap <PlayerPacket, PlayerInformEvent> playerInformEventCache;
  private final Map <String, RemoteClient> playerJoinGameRequestCache;
  private final Map <PlayerInputEvent, TimerTask> inputEventTimeouts;

  MultiplayerControllerEventCache (final PlayerInputEventTimeoutCallback timeoutCallback,
                                   final long inputEventTimeoutMillis)
  {
    Arguments.checkIsNotNull (timeoutCallback, "timeoutCallback");
    Arguments.checkIsNotNegative (inputEventTimeoutMillis, "inputEventTimeoutMillis");

    this.timeoutCallback = timeoutCallback;
    this.inputEventTimeoutMillis = inputEventTimeoutMillis;

    inputEventTimer = new Timer (TIMER_NAME, true);
    playerInputRequestEventCache = HashMultimap.create ();
    playerInformEventCache = HashMultimap.create ();
    playerJoinGameRequestCache = Collections.synchronizedMap (Maps. <String, RemoteClient>newHashMap ());
    inputEventTimeouts = Collections.synchronizedMap (Maps. <PlayerInputEvent, TimerTask>newHashMap ());
  }

  boolean add (final PlayerPacket player, final PlayerInputRequestEvent inputRequest)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (inputRequest, "inputRequest");

    addTimerTaskFor (inputRequest);
    return playerInputRequestEventCache.put (player, inputRequest);
  }

  boolean add (final PlayerPacket player, final PlayerInformEvent informEvent)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (informEvent, "informEvent");

    addTimerTaskFor (informEvent);
    return playerInformEventCache.put (player, informEvent);
  }

  void addPendingPlayerJoin (final String playerName, final RemoteClient client)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (client, "client");

    playerJoinGameRequestCache.put (playerName, client);
  }

  ImmutableSet <PlayerInputRequestEvent> inputRequestsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return ImmutableSet.copyOf (playerInputRequestEventCache.get (player));
  }

  ImmutableSet <PlayerInformEvent> informEventsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return ImmutableSet.copyOf (playerInformEventCache.get (player));
  }

  RemoteClient pendingClientFor (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.get (playerName);
  }

  boolean remove (final PlayerPacket player, final PlayerInputRequestEvent inputRequest)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (inputRequest, "inputRequest");

    cancelTimerTaskFor (inputRequest);
    return playerInputRequestEventCache.remove (player, inputRequest);
  }

  boolean remove (final PlayerPacket player, final PlayerInformEvent informEvent)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (informEvent, "informEvent");

    cancelTimerTaskFor (informEvent);
    return playerInformEventCache.remove (player, informEvent);
  }

  RemoteClient removePendingPlayerJoin (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.remove (playerName);
  }

  ImmutableSet <PlayerInputRequestEvent> removeAllInputRequestsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return cancelAll (ImmutableSet.copyOf (playerInputRequestEventCache.removeAll (player)));
  }

  ImmutableSet <PlayerInformEvent> removeAllInformEventsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return cancelAll (ImmutableSet.copyOf (playerInformEventCache.removeAll (player)));
  }

  void removeAll (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    removeAllInputRequestsFor (player);
    removeAllInformEventsFor (player);
  }

  boolean isPendingJoin (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.containsKey (playerName);
  }

  boolean hasPendingEvents (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return playerInformEventCache.containsKey (player) || playerInputRequestEventCache.containsKey (player);
  }

  void resetTimerFor (final PlayerInputEvent inputEvent)
  {
    Arguments.checkIsNotNull (inputEvent, "inputEvent");

    // add method will cancel any previous timers for this event
    addTimerTaskFor (inputEvent);
  }

  private void addTimerTaskFor (final PlayerInputEvent inputEvent)
  {
    assert inputEvent != null;

    cancelTimerTaskFor (inputEvent);
    final TimerTask task = new EventTimerTask (inputEvent);
    inputEventTimer.schedule (task, inputEventTimeoutMillis);
    inputEventTimeouts.put (inputEvent, task);
  }

  private boolean cancelTimerTaskFor (final PlayerInputEvent inputEvent)
  {
    assert inputEvent != null;

    if (!inputEventTimeouts.containsKey (inputEvent)) return false;

    return inputEventTimeouts.remove (inputEvent).cancel ();
  }

  private <T extends PlayerInputEvent> ImmutableSet <T> cancelAll (final ImmutableSet <T> inputEvents)
  {
    assert inputEvents != null;

    for (final T next : inputEvents)
    {
      cancelTimerTaskFor (next);
    }

    return inputEvents;
  }

  interface PlayerInputEventTimeoutCallback
  {
    void onEventTimedOut (PlayerInputEvent inputEvent);
  }

  private class EventTimerTask extends TimerTask
  {
    private final PlayerInputEvent inputEvent;

    private EventTimerTask (final PlayerInputEvent inputEvent)
    {
      assert inputEvent != null;

      this.inputEvent = inputEvent;
    }

    @Override
    public void run ()
    {
      timeoutCallback.onEventTimedOut (inputEvent);
    }
  }
}
