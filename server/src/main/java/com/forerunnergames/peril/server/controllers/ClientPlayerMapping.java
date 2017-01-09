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

import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

//@formatter:off
/**
 * Handles the server-side mapping of clients to players and players to some unique server identifier.
 * A "player", represented as PlayerPackets from server's perspective but corresponding to a single Player
 * in Core, may be in one of three states at any given time:
 *
 * "Bound"    : The player is currently attached to a client (human or AI) and is available to receive events.
 * "Mapped"   : The player was or is currently bound to a client and has a unique server id (sent to new players
 *              as a "playerSecretId") mapped to it. When in this state, only a client with the valid secret ID
 *              may be rebound to the player.
 * "Unmapped" : The client originally controlling the player has quit or fully disconnected and the ID mapping
 *              has been cleared. When in this state, the player can be rebound to a new client via the
 *              {@link #bind(RemoteClient, PlayerPacket)} method; a new secret ID will be generated for the player
 *              now controlled by the new client. Note that in order for a bound or mapped player to enter the unmapped
 *              state, {@link #unmap(PlayerPacket)} or {@link #unmap(UUID)} must be called by the consumer.
 *
 * The logical relationship between the three states is as follows:
 * (where => is a logical "implies")
 *
 * Bound => Mapped
 * Mapped => !Unmapped
 */
//@formatter:on
public final class ClientPlayerMapping
{
  private static final PlayersBySentiencePredicate humanPredicate = new PlayersBySentiencePredicate (
          PersonSentience.HUMAN);
  private static final PlayersBySentiencePredicate aiPredicate = new PlayersBySentiencePredicate (PersonSentience.AI);
  private final BiMap <RemoteClient, PlayerPacket> clientsToPlayers;
  private final BiMap <UUID, PlayerPacket> serverIdsToPlayers;
  private final Set <PlayerPacket> players;

  public ClientPlayerMapping (final int playerLimit)
  {
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    clientsToPlayers = Maps.synchronizedBiMap (HashBiMap.<RemoteClient, PlayerPacket> create (playerLimit));
    serverIdsToPlayers = Maps.synchronizedBiMap (HashBiMap.<UUID, PlayerPacket> create (playerLimit));
    players = Sets.newConcurrentHashSet ();
  }

  /**
   * Binds the given client to the given player.
   *
   * @return the player previously bound to this client, if one existed.
   */
  public Optional <PlayerPacket> bind (final RemoteClient client, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (player, "player");

    // map player to a new unique server id, if necessary
    if (!isMapped (player)) map (UUID.randomUUID (), player);

    // if player already exists in players, remove it so the new packet with updated attributes can be added
    if (players.contains (player)) players.remove (player);

    players.add (player);

    final Optional <PlayerPacket> previousPlayer = Optional.fromNullable (clientsToPlayers.forcePut (client, player));
    return previousPlayer;
  }

  public boolean makeCurrent (final PlayerPacket player)
  {
    // remove previous value from player set, if existent;
    // otherwise, the new packet will NOT be added since they evaluate as equal
    final boolean exists = players.remove (player);
    if (!exists) return false;

    // re-add new value
    players.add (player);

    // update bound client <=> player mapping, if necessary
    final BiMap <PlayerPacket, RemoteClient> playersToClients = clientsToPlayers.inverse ();
    if (playersToClients.containsKey (player))
    {
      final RemoteClient client = playersToClients.get (player);
      clientsToPlayers.forcePut (client, player);
    }

    // update id <=> player mapping, if necessary
    final BiMap <PlayerPacket, UUID> playersToServerIds = serverIdsToPlayers.inverse ();
    if (playersToServerIds.containsKey (player))
    {
      final UUID id = playersToServerIds.get (player);
      playersToServerIds.forcePut (player, id);
    }

    return true;
  }

  /**
   * Looks up the PlayerPacket mapped to the given client. This method will query Core for updated player data before
   * returning mapped PlayerPacket.
   *
   * @throws RegisteredClientPlayerNotFoundException
   *           if the player no longer exists in the core player model
   */
  public Optional <PlayerPacket> playerFor (final RemoteClient client) throws RegisteredClientPlayerNotFoundException
  {
    Arguments.checkIsNotNull (client, "client");

    if (!clientsToPlayers.containsKey (client)) return Optional.absent ();

    final PlayerPacket oldPlayerPacket = clientsToPlayers.get (client);

    // fetch updated player data from core
    // syncPlayerData ();

    final Optional <PlayerPacket> newPlayerQuery = Optional.fromNullable (clientsToPlayers.get (client));
    if (!newPlayerQuery.isPresent ())
    {
      throw new RegisteredClientPlayerNotFoundException (oldPlayerPacket.getName (), client);
    }

    return newPlayerQuery;
  }

  public Optional <RemoteClient> clientFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    // syncPlayerData ();
    return Optional.fromNullable (clientsToPlayers.inverse ().get (player));
  }

  public Optional <PlayerPacket> playerFor (final UUID serverPlayerId)
  {
    Arguments.checkIsNotNull (serverPlayerId, "serverPlayerId");

    return Optional.fromNullable (serverIdsToPlayers.get (serverPlayerId));
  }

  public Optional <UUID> serverIdFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return Optional.fromNullable (serverIdsToPlayers.inverse ().get (player));
  }

  public boolean isMapped (final PlayerPacket player)
  {
    return serverIdsToPlayers.inverse ().containsKey (player);
  }

  public boolean isBound (final PlayerPacket player)
  {
    return clientsToPlayers.inverse ().containsKey (player);
  }

  public boolean existsPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return playerWith (name).isPresent ();
  }

  public boolean existsClientFor (final PlayerPacket player)
  {
    return clientsToPlayers.inverse ().containsKey (player);
  }

  public boolean existsClient (final RemoteClient client)
  {
    return clientsToPlayers.containsKey (client);
  }

  public boolean existsPlayer (final PlayerPacket player)
  {
    final Optional <PlayerPacket> playerMaybe = playerWith (player.getName ());
    return playerMaybe.isPresent () && player.equals (playerMaybe.get ());
  }

  public boolean areAllPlayersBound ()
  {
    for (final PlayerPacket player : players)
    {
      if (!clientsToPlayers.containsValue (player)) return false;
    }

    return true;
  }

  public Optional <PlayerPacket> playerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    // syncPlayerData ();

    for (final PlayerPacket player : players)
    {
      if (player.hasName (name)) return Optional.of (player);
    }

    return Optional.absent ();
  }

  public ImmutableSet <PlayerPacket> humanPlayers ()
  {
    // syncPlayerData ();
    return filter (clientsToPlayers.values (), humanPredicate);
  }

  public ImmutableSet <PlayerPacket> aiPlayers ()
  {
    // syncPlayerData ();
    return filter (clientsToPlayers.values (), aiPredicate);
  }

  public ImmutableSet <PlayerPacket> humanPlayersExcept (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    // syncPlayerData ();
    return ImmutableSet.copyOf (Sets.filter (Sets.difference (clientsToPlayers.values (), ImmutableSet.of (player)),
                                             humanPredicate));
  }

  public ImmutableSet <PlayerPacket> aiPlayersExcept (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    // syncPlayerData ();
    return ImmutableSet
            .copyOf (Sets.filter (Sets.difference (clientsToPlayers.values (), ImmutableSet.of (player)), aiPredicate));
  }

  public ImmutableSet <PlayerPacket> players ()
  {
    // syncPlayerData ();
    return ImmutableSet.copyOf (clientsToPlayers.values ());
  }

  public ImmutableSet <RemoteClient> clients ()
  {
    // syncPlayerData ();
    return ImmutableSet.copyOf (clientsToPlayers.keySet ());
  }

  public ImmutableSet <PlayerPacket> unmappedPlayers ()
  {
    // syncPlayerData ();
    return ImmutableSet.copyOf (Sets.difference (players, serverIdsToPlayers.values ()));
  }

  public ImmutableSet <PlayerPacket> unmappedHumanPlayers ()
  {
    // syncPlayerData ();
    return filter (unmappedPlayers (), humanPredicate);
  }

  public ImmutableSet <PlayerPacket> unmappedAiPlayers ()
  {
    // syncPlayerData ();
    return filter (unmappedPlayers (), aiPredicate);
  }

  /**
   * Unmaps the given client from its assigned player. The resulting, unassigned player packet is retained as "unmapped"
   * for future use.
   */
  public Optional <PlayerPacket> unbind (final RemoteClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    final Optional <PlayerPacket> removedPlayer = Optional.fromNullable (clientsToPlayers.remove (client));
    return removedPlayer;
  }

  public Optional <UUID> unmap (final PlayerPacket player)
  {
    final Optional <UUID> playerId = serverIdFor (player);
    if (playerId.isPresent ())
    {
      unmap (playerId.get ());
    }

    return playerId;
  }

  public Optional <PlayerPacket> unmap (final UUID serverId)
  {
    final Optional <PlayerPacket> removedPlayer = Optional.fromNullable (serverIdsToPlayers.remove (serverId));
    if (removedPlayer.isPresent ())
    {
      assert players.contains (removedPlayer.get ());
      clientsToPlayers.inverse ().remove (removedPlayer.get ());
    }

    return removedPlayer;
  }

  /**
   * Removes the given player, regardless of whether or not it has current client mapping.
   */
  public void remove (final PlayerPacket player)
  {
    clientsToPlayers.inverse ().remove (player);
    serverIdsToPlayers.inverse ().remove (player);
    players.remove (player);
  }

  public void reset ()
  {
    clientsToPlayers.clear ();
    serverIdsToPlayers.clear ();
    players.clear ();
  }

  private static <T> ImmutableSet <T> filter (final Collection <T> objects, final Predicate <T> predicate)
  {
    return ImmutableSet.copyOf (Collections2.filter (objects, predicate));
  }

  private void map (final UUID serverId, final PlayerPacket player)
  {
    serverIdsToPlayers.forcePut (serverId, player);
  }

  @Override
  public String toString ()
  {
    return clientsToPlayers.toString ();
  }

  private static class PlayersBySentiencePredicate implements Predicate <PlayerPacket>
  {
    private final PersonSentience sentience;

    public PlayersBySentiencePredicate (final PersonSentience sentience)
    {
      Arguments.checkIsNotNull (sentience, "sentience");

      this.sentience = sentience;
    }

    @Override
    public boolean apply (final PlayerPacket input)
    {
      Arguments.checkIsNotNull (input, "input");

      return input.has (sentience);
    }
  }
}
