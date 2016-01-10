package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping.RegisteredClientPlayerNotFoundException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.Remote;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ClientSpectatorMapping
{
  private final BiMap <Remote, SpectatorPacket> clientsToSpectators;

  public ClientSpectatorMapping (final int spectatorLimit)
  {
    Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");

    clientsToSpectators = Maps.synchronizedBiMap (HashBiMap.<Remote, SpectatorPacket> create (spectatorLimit));
  }

  public Optional <SpectatorPacket> put (final Remote client, final SpectatorPacket spectator)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (spectator, "spectator");

    return Optional.fromNullable (clientsToSpectators.forcePut (client, spectator));
  }

  /**
   * Looks up the SpectatorPacket mapped to the given client. This method will query Core for updated spectator data
   * before returning mapped SpectatorPacket.
   *
   * @throws RegisteredClientPlayerNotFoundException
   *           if the spectator no longer exists in the core spectator model
   */
  public Optional <SpectatorPacket> spectatorFor (final Remote client) throws RegisteredClientSpectatorNotFoundException
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

  public Optional <Remote> clientFor (final SpectatorPacket spectator)
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

  public ImmutableSet <Remote> clients ()
  {
    return ImmutableSet.copyOf (clientsToSpectators.keySet ());
  }

  public Optional <SpectatorPacket> remove (final Remote client)
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

    RegisteredClientSpectatorNotFoundException (final String spectatorName, final Remote client)
    {
      Arguments.checkIsNotNull (spectatorName, "spectatorName");
      Arguments.checkIsNotNull (client, "client");

      message = Strings.format ("Player [{}] not found for client [{}].", spectatorName, client);
    }
  }
}
