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

package com.forerunnergames.peril.server.communicators;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.server.controllers.ClientPlayerMapping;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientCommunicator;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HumanPlayerCommunicator implements PlayerCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (HumanPlayerCommunicator.class);
  private final RemoteClientCommunicator clientCommunicator;

  public HumanPlayerCommunicator (final RemoteClientCommunicator clientCommunicator)
  {
    Arguments.checkIsNotNull (clientCommunicator, "clientCommunicator");

    this.clientCommunicator = clientCommunicator;
  }

  @Override
  public void sendToPlayer (final PlayerPacket player, final Event message, final ClientPlayerMapping mapping)
  {
    Arguments.checkIsNotNull (mapping, "mapping");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (message, "message");

    final Optional <RemoteClient> clientQuery = mapping.clientFor (player);

    if (!clientQuery.isPresent ())
    {
      log.warn ("Ignoring attempt to send message: [{}] to disconnected player: [{}]", message, player);
      return;
    }

    log.debug ("Sending message: [{}] to human player: [{}]", message, player);

    sendTo (clientQuery.get (), message);
  }

  @Override
  public void sendToAllPlayers (final Event message, final ClientPlayerMapping mapping)
  {
    Arguments.checkIsNotNull (mapping, "mapping");
    Arguments.checkIsNotNull (message, "message");

    log.debug ("Sending message: [{}] to all human players.", message);

    for (final PlayerPacket player : mapping.humanPlayers ())
    {
      sendToPlayer (player, message, mapping);
    }
  }

  @Override
  public void sendToAllPlayersExcept (final PlayerPacket player, final Event message, final ClientPlayerMapping mapping)
  {
    Arguments.checkIsNotNull (mapping, "mapping");
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (message, "message");

    log.debug ("Sending message: [{}] to all human players except: [{}].", message, player);

    for (final PlayerPacket targetPlayer : mapping.humanPlayersExcept (player))
    {
      sendToPlayer (targetPlayer, message, mapping);
    }
  }

  @Override
  public void sendTo (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    log.trace ("Sending object: [{}] to human client: [{}].", object, client);

    clientCommunicator.sendTo (client, object);
  }

  @Override
  public void sendToAll (final Object object)
  {
    Arguments.checkIsNotNull (object, "object");

    log.trace ("Sending object: [{}] to all human clients.", object);

    clientCommunicator.sendToAll (object);
  }

  @Override
  public void sendToAllExcept (final RemoteClient client, final Object object)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (object, "object");

    log.trace ("Sending object: [{}] to all human clients except: [{}].", object, client);

    clientCommunicator.sendToAllExcept (client, object);
  }
}
