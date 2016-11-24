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

package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.Remote;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientPlayerMapping
{
  private static final Logger log = LoggerFactory.getLogger (ClientPlayerMapping.class);
  private final BiMap <Remote, PlayerPacket> clientsToPlayers;
  private final CoreCommunicator coreCommunicator;

  public ClientPlayerMapping (final CoreCommunicator coreCommunicator, final int playerLimit)
  {
    Arguments.checkIsNotNull (coreCommunicator, "coreCommunicator");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    this.coreCommunicator = coreCommunicator;

    clientsToPlayers = Maps.synchronizedBiMap (HashBiMap.<Remote, PlayerPacket> create (playerLimit));
  }

  public Optional <PlayerPacket> put (final Remote client, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (player, "player");

    return Optional.fromNullable (clientsToPlayers.forcePut (client, player));
  }

  /**
   * Looks up the PlayerPacket mapped to the given client. This method will query Core for updated player data before
   * returning mapped PlayerPacket.
   *
   * @throws RegisteredClientPlayerNotFoundException
   *           if the player no longer exists in the core player model
   */
  public Optional <PlayerPacket> playerFor (final Remote client) throws RegisteredClientPlayerNotFoundException
  {
    Arguments.checkIsNotNull (client, "client");

    if (!clientsToPlayers.containsKey (client)) return Optional.absent ();

    final PlayerPacket oldPlayerPacket = clientsToPlayers.get (client);
    // fetch updated player from core
    syncPlayerData ();
    final Optional <PlayerPacket> newPlayerQuery = Optional.fromNullable (clientsToPlayers.get (client));
    if (!newPlayerQuery.isPresent ())
    {
      throw new RegisteredClientPlayerNotFoundException (oldPlayerPacket.getName (), client);
    }
    return newPlayerQuery;
  }

  public Optional <Remote> clientFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    syncPlayerData ();
    return Optional.fromNullable (clientsToPlayers.inverse ().get (player));
  }

  public boolean existsPlayerWith (final String name)
  {
    return playerWith (name).isPresent ();
  }

  public Optional <PlayerPacket> playerWith (final String name)
  {
    syncPlayerData ();
    for (final PlayerPacket player : clientsToPlayers.values ())
    {
      if (player.hasName (name)) return Optional.of (player);
    }
    return Optional.absent ();
  }

  public ImmutableSet <PlayerPacket> humanPlayers ()
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (Collections2.filter (clientsToPlayers.values (), new PlayersBySentiencePredicate (
            PersonSentience.HUMAN)));
  }

  public ImmutableSet <PlayerPacket> aiPlayers ()
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (Collections2.filter (clientsToPlayers.values (), new PlayersBySentiencePredicate (
            PersonSentience.AI)));
  }

  public ImmutableSet <PlayerPacket> humanPlayersExcept (final PlayerPacket player)
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (Sets.filter (Sets.difference (clientsToPlayers.values (), ImmutableSet.of (player)),
                                             new PlayersBySentiencePredicate (PersonSentience.HUMAN)));
  }

  public ImmutableSet <PlayerPacket> aiPlayersExcept (final PlayerPacket player)
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (Sets.filter (Sets.difference (clientsToPlayers.values (), ImmutableSet.of (player)),
                                             new PlayersBySentiencePredicate (PersonSentience.AI)));
  }

  public ImmutableSet <PlayerPacket> players ()
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (clientsToPlayers.values ());
  }

  public ImmutableSet <Remote> clients ()
  {
    syncPlayerData ();
    return ImmutableSet.copyOf (clientsToPlayers.keySet ());
  }

  public Optional <PlayerPacket> remove (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return Optional.fromNullable (clientsToPlayers.remove (client));
  }

  private void syncPlayerData ()
  {
    final ImmutableSet <PlayerPacket> updatedPlayerData = coreCommunicator.fetchCurrentPlayerData ();
    for (final PlayerPacket current : updatedPlayerData)
    {
      // PlayerPackets by contract must evaluate as equal for the same player, so get will work here even
      // with updated data.
      final Optional <Remote> client = Optional.fromNullable (clientsToPlayers.inverse ().get (current));
      if (!client.isPresent ())
      {
        log.warn ("Received player [{}] from core with no client mapping.", current);
        continue;
      }
      clientsToPlayers.forcePut (client.get (), current);
    }
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
      return input.has (sentience);
    }
  }

  /**
   * Thrown to indicate that the player registered to a client no longer exists. This is an exceptional case (and likely
   * the result of a bug or state violation in core) so it needs to be handled by server appropriately.
   */
  final class RegisteredClientPlayerNotFoundException extends Exception
  {
    final String message;

    RegisteredClientPlayerNotFoundException (final String playerName, final Remote client)
    {
      Arguments.checkIsNotNull (playerName, "playerName");
      Arguments.checkIsNotNull (client, "client");

      message = Strings.format ("Player [{}] not found for client [{}].", playerName, client);
    }
  }
}
