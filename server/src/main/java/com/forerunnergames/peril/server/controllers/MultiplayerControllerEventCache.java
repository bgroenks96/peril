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

package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
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
  private final Multimap <PlayerPacket, PlayerInputEvent> playerInputEventCache;
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
    playerInputEventCache = HashMultimap.create ();
    playerJoinGameRequestCache = Collections.synchronizedMap (Maps. <String, RemoteClient>newHashMap ());
    inputEventTimeouts = Collections.synchronizedMap (Maps. <PlayerInputEvent, TimerTask>newHashMap ());
  }

  boolean add (final PlayerPacket player, final PlayerInputEvent inputRequest)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (inputRequest, "inputRequest");

    addTimerTaskFor (inputRequest);
    return playerInputEventCache.put (player, inputRequest);
  }

  void addPendingPlayerJoin (final String playerName, final RemoteClient client)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (client, "client");

    playerJoinGameRequestCache.put (playerName, client);
  }

  ImmutableSet <PlayerInputEvent> inputEventsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return ImmutableSet.copyOf (playerInputEventCache.get (player));
  }

  RemoteClient pendingClientFor (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.get (playerName);
  }

  boolean remove (final PlayerPacket player, final PlayerInputEvent inputEvent)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (inputEvent, "inputEvent");

    cancelTimerTaskFor (inputEvent);
    return playerInputEventCache.remove (player, inputEvent);
  }

  RemoteClient removePendingPlayerJoin (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.remove (playerName);
  }

  void remove (final PlayerInputEvent inputEvent)
  {
    Arguments.checkIsNotNull (inputEvent, "inputEvent");

    cancelTimerTaskFor (inputEvent);

    // we don't know the specific type of the event, so try to remove from both caches
    final PlayerPacket player = inputEvent.getPerson ();
    if (!playerInputEventCache.containsKey (player)) return;

    playerInputEventCache.remove (player, inputEvent);
  }

  ImmutableSet <PlayerInputEvent> removeAllInputEventsFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return cancelAll (ImmutableSet.copyOf (playerInputEventCache.removeAll (player)));
  }

  boolean isPendingJoin (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return playerJoinGameRequestCache.containsKey (playerName);
  }

  boolean hasPendingEvents (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return playerInputEventCache.containsKey (player);
  }

  void resetTimerFor (final PlayerInputEvent inputEvent)
  {
    Arguments.checkIsNotNull (inputEvent, "inputEvent");

    // add method will cancel any previous timers for this event
    addTimerTaskFor (inputEvent);
  }

  void cancelAllTimersFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!playerInputEventCache.containsKey (player)) return;

    cancelAll (playerInputEventCache.get (player));
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

  private <T extends PlayerInputEvent> ImmutableSet <T> cancelAll (final Collection <T> inputEvents)
  {
    assert inputEvents != null;

    for (final T next : inputEvents)
    {
      cancelTimerTaskFor (next);
    }

    return ImmutableSet.copyOf (inputEvents);
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
