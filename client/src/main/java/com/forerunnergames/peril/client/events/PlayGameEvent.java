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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.collect.ImmutableSet;

public final class PlayGameEvent implements LocalEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;
  private final ImmutableSet <PlayerPacket> playersInGame;
  private final PlayMap playMap;

  public PlayGameEvent (final GameServerConfiguration gameServerConfig,
                        final ClientConfiguration clientConfig,
                        final ImmutableSet <PlayerPacket> playersInGame,
                        final PlayMap playMap)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkIsNotNull (playMap, "playMap");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
    this.playersInGame = playersInGame;
    this.playMap = playMap;
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
    return playersInGame;
  }

  public MapMetadata getMapMetadata ()
  {
    return gameServerConfig.getMapMetadata ();
  }

  public PlayMap getPlayMap ()
  {
    return playMap;
  }

  public MapMetadata getPlayMapMetadata ()
  {
    return playMap.getMapMetadata ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Game Server Configuration: {} | Client Configuration: {} | Players In Game: {} | Play Map Actor: {}",
                           getClass ().getSimpleName (), gameServerConfig, clientConfig, playersInGame, playMap);
  }
}
