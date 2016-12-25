package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInformEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class MultiplayerControllerEventCache
{
  private final Multimap <PlayerPacket, PlayerInputRequestEvent> playerInputRequestEventCache;
  private final Multimap <PlayerPacket, PlayerInformEvent> playerInformEventCache;
  private final Map <String, RemoteClient> playerJoinGameRequestCache;

  MultiplayerControllerEventCache ()
  {
    playerInputRequestEventCache = HashMultimap.create ();
    playerInformEventCache = HashMultimap.create ();
    playerJoinGameRequestCache = Collections.synchronizedMap (new HashMap <String, RemoteClient> ());
  }

  boolean add (final PlayerPacket player, final PlayerInputRequestEvent inputRequest)
  {
    return playerInputRequestEventCache.put (player, inputRequest);
  }

  boolean add (final PlayerPacket player, final PlayerInformEvent informEvent)
  {
    return playerInformEventCache.put (player, informEvent);
  }

  void addPendingPlayerJoin (final String playerName, final RemoteClient client)
  {
    playerJoinGameRequestCache.put (playerName, client);
  }

  ImmutableSet <PlayerInputRequestEvent> inputRequestsFor (final PlayerPacket player)
  {
    return ImmutableSet.copyOf (playerInputRequestEventCache.get (player));
  }

  ImmutableSet <PlayerInformEvent> informEventsFor (final PlayerPacket player)
  {
    return ImmutableSet.copyOf (playerInformEventCache.get (player));
  }

  RemoteClient pendingClientFor (final String playerName)
  {
    return playerJoinGameRequestCache.get (playerName);
  }

  boolean remove (final PlayerPacket player, final PlayerInputRequestEvent inputRequest)
  {
    return playerInputRequestEventCache.remove (player, inputRequest);
  }

  boolean remove (final PlayerPacket player, final PlayerInformEvent informEvent)
  {
    return playerInformEventCache.remove (player, informEvent);
  }

  RemoteClient removePendingPlayerJoin (final String playerName)
  {
    return playerJoinGameRequestCache.remove (playerName);
  }

  ImmutableSet <PlayerInputRequestEvent> removeAllInputRequestsFor (final PlayerPacket player)
  {
    return ImmutableSet.copyOf (playerInputRequestEventCache.removeAll (player));
  }

  ImmutableSet <PlayerInformEvent> removeAllInformEventsFor (final PlayerPacket player)
  {
    return ImmutableSet.copyOf (playerInformEventCache.removeAll (player));
  }

  void removeAll (final PlayerPacket player)
  {
    removeAllInputRequestsFor (player);
    removeAllInformEventsFor (player);
  }

  boolean isPendingJoin (final String playerName)
  {
    return playerJoinGameRequestCache.containsKey (playerName);
  }

  boolean hasPendingEvents (final PlayerPacket player)
  {
    return playerInformEventCache.containsKey (player) || playerInputRequestEventCache.containsKey (player);
  }
}
