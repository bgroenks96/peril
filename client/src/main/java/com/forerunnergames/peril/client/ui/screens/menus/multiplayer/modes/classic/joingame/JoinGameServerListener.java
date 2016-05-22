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

package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

public interface JoinGameServerListener
{
  void onJoinStart (final String playerName, final ServerConfiguration configuration);

  void onConnectToServerSuccess (final ServerConfiguration configuration);

  void onJoinGameServerSuccess (final GameServerConfiguration gameServerConfiguration,
                                final ClientConfiguration clientConfiguration);

  void onPlayerJoinGameSuccess (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersInGame);

  void onConnectToServerFailure (final ServerConfiguration configuration, final String reason);

  void onJoinGameServerFailure (final ClientConfiguration configuration, final String reason);

  void onPlayerJoinGameFailure (final String playerName, final PlayerJoinGameDeniedEvent.Reason reason);

  void onJoinFinish (final GameServerConfiguration gameServerConfiguration,
                     final ClientConfiguration clientConfiguration,
                     final ImmutableSet <PlayerPacket> playersInGame);
}
