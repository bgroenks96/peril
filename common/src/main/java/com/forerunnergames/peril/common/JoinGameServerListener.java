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

package com.forerunnergames.peril.common;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

public interface JoinGameServerListener
{
  void onJoinStart (final String playerName, final ServerConfiguration config);

  void onConnectToServerSuccess (final ServerConfiguration config);

  void onJoinGameServerSuccess (final GameServerConfiguration gameServerConfig,
                                final ClientConfiguration clientConfig,
                                final String playerName);

  void onPlayerJoinGameSuccess (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersInGame);

  void onConnectToServerFailure (final ServerConfiguration config, final String reason);

  void onJoinGameServerFailure (final ClientConfiguration config, final String reason);

  void onPlayerJoinGameFailure (final String playerName, final PlayerJoinGameDeniedEvent.Reason reason);

  void onJoinFinish (final GameServerConfiguration gameServerConfig,
                     final ClientConfiguration clientConfig,
                     final ImmutableSet <PlayerPacket> players);
}
