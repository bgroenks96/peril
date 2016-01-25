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

package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.client.ClientCommunicator;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultPlayerCommunicator implements PlayerCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (PlayerCommunicator.class);
  private final ClientCommunicator clientCommunicator;

  public DefaultPlayerCommunicator (final ClientCommunicator clientCommunicator)
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

  @Override
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

    sendTo (clientQuery.get (), object);
  }

  @Override
  public void sendToAllPlayers (final Object object, final ClientPlayerMapping clientPlayerMapping)
  {
    Arguments.checkIsNotNull (clientPlayerMapping, "clientPlayerMapping");
    Arguments.checkIsNotNull (object, "object");

    for (final PlayerPacket player : clientPlayerMapping.players ())
    {
      sendToPlayer (player, object, clientPlayerMapping);
    }
  }

  @Override
  public void sendToAllPlayersExcept (final PlayerPacket player,
                                      final Object object,
                                      final ClientPlayerMapping clientPlayerMapping)
  {
    Arguments.checkIsNotNull (clientPlayerMapping, "clientPlayerMapping");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (object, "object");

    for (final PlayerPacket targetPlayer : clientPlayerMapping.playersExcept (player))
    {
      sendToPlayer (targetPlayer, object, clientPlayerMapping);
    }
  }
}
