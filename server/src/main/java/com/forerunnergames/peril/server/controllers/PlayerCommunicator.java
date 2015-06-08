package com.forerunnergames.peril.server.controllers;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.net.ClientCommunicator;
import com.forerunnergames.tools.net.Remote;

import com.google.common.collect.ImmutableMap;

public final class PlayerCommunicator implements ClientCommunicator
{
  private final ClientCommunicator clientCommunicator;

  public PlayerCommunicator (final ClientCommunicator clientCommunicator)
  {
    this.clientCommunicator = clientCommunicator;
  }

  public void sendToPlayer (final ImmutableMap <PlayerPacket, Remote> playersToClients,
                            final PlayerPacket player,
                            final Object object)
  {
    Arguments.checkIsNotNull (playersToClients, "playersToClients");
    Arguments.checkHasNoNullKeysOrValues (playersToClients, "playersToClients");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (object, "object");
    Preconditions.checkIsTrue (playersToClients.containsKey (player),
                               "player -> client map does not have player as key.");

    clientCommunicator.sendTo (playersToClients.get (player), object);
  }

  public void sendToAllPlayers (final ImmutableMap <PlayerPacket, Remote> playersToClients, final Object object)
  {
    Arguments.checkIsNotNull (playersToClients, "playersToClients");
    Arguments.checkHasNoNullKeysOrValues (playersToClients, "playersToClients");
    Arguments.checkIsNotNull (object, "object");

    for (final PlayerPacket player : playersToClients.keySet ())
    {
      sendToPlayer (playersToClients, player, object);
    }
  }

  public void sendToAllPlayersExcept (final ImmutableMap <PlayerPacket, Remote> playersToClients,
                                      final PlayerPacket player,
                                      Object object)
  {
    Arguments.checkIsNotNull (playersToClients, "playersToClients");
    Arguments.checkHasNoNullKeysOrValues (playersToClients, "playersToClients");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (object, "object");
    Preconditions.checkIsTrue (playersToClients.containsKey (player),
                               "player -> client map does not have player as key.");

    for (final PlayerPacket targetPlayer : playersToClients.keySet ())
    {
      if (targetPlayer.isNot (player)) sendToPlayer (playersToClients, targetPlayer, object);
    }
  }

  @Override
  public void sendTo (final Remote client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    this.clientCommunicator.sendTo (client, object);
  }

  @Override
  public void sendToAll (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    this.clientCommunicator.sendToAll (object);
  }

  @Override
  public void sendToAllExcept (final Remote client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    this.clientCommunicator.sendToAllExcept (client, object);
  }
}
