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

package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

public final class JoinGameServerSuccessEvent implements SuccessEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;
  private final Collection <PlayerPacket> playersInGame;

  public JoinGameServerSuccessEvent (final GameServerConfiguration gameServerConfig,
                                     final ClientConfiguration clientConfig,
                                     final Collection <PlayerPacket> playersInGame)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
    this.playersInGame = playersInGame;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  public ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return ImmutableSet.copyOf (playersInGame);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Server Configuration: %2$s | Client Configuration: %3$s | Players In Game: %4$s",
                          getClass ().getSimpleName (), gameServerConfig, clientConfig, playersInGame);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerSuccessEvent ()
  {
    gameServerConfig = null;
    clientConfig = null;
    playersInGame = null;
  }
}
