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

import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ClientSpectatorMapping
{
  private final BiMap <RemoteClient, SpectatorPacket> clientsToSpectators;

  public ClientSpectatorMapping (final int spectatorLimit)
  {
    Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

    clientsToSpectators = Maps.synchronizedBiMap (HashBiMap.<RemoteClient, SpectatorPacket> create (spectatorLimit));
  }

  public Optional <SpectatorPacket> put (final RemoteClient client, final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (spectator, "spectator");

    return Optional.fromNullable (clientsToSpectators.forcePut (client, spectator));
  }

  /**
   * Looks up the SpectatorPacket mapped to the given client. This method will query Core for updated spectator data
   * before returning mapped SpectatorPacket.
   *
   * @throws RegisteredClientSpectatorNotFoundException
   *           if the spectator no longer exists in the core spectator model
   */
  public Optional <SpectatorPacket> spectatorFor (final RemoteClient client)
          throws RegisteredClientSpectatorNotFoundException
  {
    Arguments.checkIsNotNull (client, "client");

    if (!clientsToSpectators.containsKey (client)) return Optional.absent ();

    final SpectatorPacket oldSpectatorPacket = clientsToSpectators.get (client);
    final Optional <SpectatorPacket> newPlayerQuery = Optional.fromNullable (clientsToSpectators.get (client));
    if (!newPlayerQuery.isPresent ())
    {
      throw new RegisteredClientSpectatorNotFoundException (oldSpectatorPacket.getName (), client);
    }
    return newPlayerQuery;
  }

  public boolean existsSpectatorWith (final String name)
  {
    return spectatorWith (name).isPresent ();
  }

  public Optional <SpectatorPacket> spectatorWith (final String name)
  {
    for (final SpectatorPacket spectator : clientsToSpectators.values ())
    {
      if (spectator.hasName (name)) return Optional.of (spectator);
    }
    return Optional.absent ();
  }

  public Optional <RemoteClient> clientFor (final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (spectator, "spectator");

    return Optional.fromNullable (clientsToSpectators.inverse ().get (spectator));
  }

  public ImmutableSet <SpectatorPacket> spectators ()
  {
    return ImmutableSet.copyOf (clientsToSpectators.values ());
  }

  public ImmutableSet <SpectatorPacket> spectatorsExcept (final SpectatorPacket spectator)
  {
    return ImmutableSet.copyOf (Sets.difference (clientsToSpectators.values (), ImmutableSet.of (spectator)));
  }

  public ImmutableSet <RemoteClient> clients ()
  {
    return ImmutableSet.copyOf (clientsToSpectators.keySet ());
  }

  public Optional <SpectatorPacket> remove (final RemoteClient client)
  {
    Arguments.checkIsNotNull (client, "client");

    return Optional.fromNullable (clientsToSpectators.remove (client));
  }

  public int spectatorCount ()
  {
    return clientsToSpectators.size ();
  }

  @Override
  public String toString ()
  {
    return clientsToSpectators.toString ();
  }

  /**
   * Thrown to indicate that the spectator registered to a client no longer exists.
   */
  final class RegisteredClientSpectatorNotFoundException extends Exception
  {
    final String message;

    RegisteredClientSpectatorNotFoundException (final String spectatorName, final RemoteClient client)
    {
      Arguments.checkIsNotNull (spectatorName, "spectatorName");
      Arguments.checkIsNotNull (client, "client");

      message = Strings.format ("Player [{}] not found for client [{}].", spectatorName, client);
    }
  }
}
