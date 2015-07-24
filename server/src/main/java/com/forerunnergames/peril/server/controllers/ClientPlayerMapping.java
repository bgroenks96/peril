package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.communicators.CoreCommunicator;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.Remote;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class ClientPlayerMapping
{
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
    final Optional <PlayerPacket> newPlayerQuery = updatedPlayerDataFor (client);
    if (!newPlayerQuery.isPresent ())
    {
      throw new RegisteredClientPlayerNotFoundException (oldPlayerPacket.getName (), client);
    }
    return newPlayerQuery;
  }

  public Optional <Remote> clientFor (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return Optional.fromNullable (clientsToPlayers.inverse ().get (player));
  }

  public ImmutableSet <PlayerPacket> players ()
  {
    return ImmutableSet.copyOf (clientsToPlayers.values ());
  }

  public ImmutableSet <Remote> clients ()
  {
    return ImmutableSet.copyOf (clientsToPlayers.keySet ());
  }

  public Optional <PlayerPacket> remove (final Remote client)
  {
    Arguments.checkIsNotNull (client, "client");

    return Optional.fromNullable (clientsToPlayers.remove (client));
  }

  public Optional <Remote> remove (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return Optional.fromNullable (clientsToPlayers.inverse ().remove (player));
  }

  private Optional <PlayerPacket> updatedPlayerDataFor (final Remote client)
  {
    syncPlayerData ();
    return Optional.fromNullable (clientsToPlayers.get (client));
  }

  private void syncPlayerData ()
  {
    final ImmutableSet <PlayerPacket> updatedPlayerData = coreCommunicator.fetchCurrentPlayerData ();
    for (final PlayerPacket current : updatedPlayerData)
    {
      // PlayerPackets by contract must evaluate as equal for the same player, so get will work here even
      // with updated data.
      final Optional <Remote> client = Optional.fromNullable (clientsToPlayers.inverse ().get (current));
      // this can only indicate a serious internal bug in core, so it's worth a crash
      if (!client.isPresent ()) Exceptions.throwIllegalState ("Received unregistered player [{}] from core.", current);
      clientsToPlayers.forcePut (client.get (), current);
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

      this.message = Strings.format ("Player [{}] not found for client [{}].", playerName, client);
    }
  }
}
