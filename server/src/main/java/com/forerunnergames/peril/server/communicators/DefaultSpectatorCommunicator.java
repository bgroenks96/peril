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

import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.server.controllers.ClientSpectatorMapping;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.net.server.remote.RemoteClient;
import com.forerunnergames.tools.net.server.remote.RemoteClientCommunicator;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultSpectatorCommunicator implements SpectatorCommunicator
{
  private static final Logger log = LoggerFactory.getLogger (DefaultSpectatorCommunicator.class);
  private final RemoteClientCommunicator clientCommunicator;

  public DefaultSpectatorCommunicator (final RemoteClientCommunicator clientCommunicator)
  {
    Arguments.checkIsNotNull (clientCommunicator, "clientCommunicator");

    this.clientCommunicator = clientCommunicator;
  }

  @Override
  public void sendToSpectator (final SpectatorPacket spectator,
                               final Event message,
                               final ClientSpectatorMapping mapping)
  {
    Arguments.checkIsNotNull (spectator, "spectator");
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (mapping, "mapping");

    final Optional <RemoteClient> clientQuery = mapping.clientFor (spectator);

    if (!clientQuery.isPresent ())
    {
      log.warn ("Ignoring attempt to send [{}] to disconnected spectator [{}].", message, spectator);
      return;
    }

    sendTo (clientQuery.get (), message);
  }

  @Override
  public void sendToAllSpectators (final Event message, final ClientSpectatorMapping mapping)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (mapping, "mapping");

    for (final SpectatorPacket next : mapping.spectators ())
    {
      sendToSpectator (next, message, mapping);
    }
  }

  @Override
  public void sendToAllSpectatorsExcept (final SpectatorPacket spectator,
                                         final Event message,
                                         final ClientSpectatorMapping mapping)
  {
    Arguments.checkIsNotNull (spectator, "spectator");
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (mapping, "mapping");

    for (final SpectatorPacket next : mapping.spectatorsExcept (spectator))
    {
      sendToSpectator (next, message, mapping);
    }
  }

  @Override
  public void sendTo (final RemoteClient client, final Object msg)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendTo (client, msg);
  }

  @Override
  public void sendToAll (final Object msg)
  {
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendToAll (msg);
  }

  @Override
  public void sendToAllExcept (final RemoteClient client, final Object msg)
  {
    Arguments.checkIsNotNull (client, "client");
    Arguments.checkIsNotNull (msg, "msg");

    clientCommunicator.sendToAllExcept (client, msg);
  }
}
