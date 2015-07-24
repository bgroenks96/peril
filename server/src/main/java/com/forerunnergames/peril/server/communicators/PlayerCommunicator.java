package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientCommunicator;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerCommunicator implements ClientCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (PlayerCommunicator.class);
  private final ClientCommunicator clientCommunicator;

  public PlayerCommunicator (final ClientCommunicator clientCommunicator)
  {
    Arguments.checkIsNotNull (clientCommunicator, "clientCommunicator");

    this.clientCommunicator = clientCommunicator;
  }

  @Override
  public void sendTo (final Remote client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    clientCommunicator.sendTo (client, object);
  }

  @Override
  public void sendToAll (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    clientCommunicator.sendToAll (object);
  }

  @Override
  public void sendToAllExcept (final Remote client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    clientCommunicator.sendToAllExcept (client, object);
  }

  public void sendToPlayer (final PlayerPacket player,
                            final Object object,
                            final ClientPlayerMapping clientPlayerMapping)
  {
    Arguments.checkIsNotNull (clientPlayerMapping, "clientPlayerMapping");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (object, "object");

    final Optional <Remote> clientQuery = clientPlayerMapping.clientFor (player);

    if (!clientQuery.isPresent ())
    {
      log.warn ("Ignoring attempt to send object [{}] to disconnected player [{}]", object, player);
      return;
    }

    clientCommunicator.sendTo (clientQuery.get (), object);
  }

  public void sendToAllPlayers (final Object object, final ClientPlayerMapping clientPlayerMapping)
  {
    Arguments.checkIsNotNull (clientPlayerMapping, "clientPlayerMapping");
    Arguments.checkIsNotNull (object, "object");

    for (final PlayerPacket player : clientPlayerMapping.players ())
    {
      sendToPlayer (player, object, clientPlayerMapping);
    }
  }

  public void sendToAllPlayersExcept (final PlayerPacket player,
                                      final Object object,
                                      final ClientPlayerMapping clientPlayerMapping)
  {
    Arguments.checkIsNotNull (clientPlayerMapping, "clientPlayerMapping");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (object, "object");

    final Optional <Remote> clientQuery = clientPlayerMapping.clientFor (player);

    if (!clientQuery.isPresent ())
    {
      log.warn ("Ignoring attempt to exclude disconnected player [{}] from receiving object [{}]", player, object);
    }

    for (final PlayerPacket targetPlayer : clientPlayerMapping.players ())
    {
      if (targetPlayer.isNot (player)) sendToPlayer (targetPlayer, object, clientPlayerMapping);
    }
  }
}
