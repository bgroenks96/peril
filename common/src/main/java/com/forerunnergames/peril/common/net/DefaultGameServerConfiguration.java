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

package com.forerunnergames.peril.common.net;

import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

public final class DefaultGameServerConfiguration implements GameServerConfiguration
{
  private final String gameServerName;
  private final GameServerType gameServerType;
  private final GameConfiguration gameConfig;
  private final ServerConfiguration serverConfig;

  public DefaultGameServerConfiguration (final String gameServerName,
                                         final GameServerType gameServerType,
                                         final GameConfiguration gameConfig,
                                         final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (gameServerName, "gameServerName");
    Arguments.checkIsNotNull (gameServerType, "gameServerType");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    this.gameServerName = gameServerName;
    this.gameServerType = gameServerType;
    this.gameConfig = gameConfig;
    this.serverConfig = serverConfig;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameConfig.getGameMode ();
  }

  @Override
  public int getPlayerLimit ()
  {
    return gameConfig.getPlayerLimit ();
  }

  @Override
  public int getSpectatorLimit ()
  {
    return gameConfig.getSpectatorLimit ();
  }

  @Override
  public int getWinPercentage ()
  {
    return gameConfig.getWinPercentage ();
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return gameConfig.getInitialCountryAssignment ();
  }

  @Override
  public String getMapName ()
  {
    return gameConfig.getMapName ();
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return gameConfig.getMapMetadata ();
  }

  @Override
  public MapType getMapType ()
  {
    return gameConfig.getMapType ();
  }

  @Override
  public String getServerAddress ()
  {
    return serverConfig.getServerAddress ();
  }

  @Override
  public int getServerTcpPort ()
  {
    return serverConfig.getServerTcpPort ();
  }

  @Override
  public String getGameServerName ()
  {
    return gameServerName;
  }

  @Override
  public GameServerType getGameServerType ()
  {
    return gameServerType;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Server Name: %2$s | Game Server Type: %3$s | Game Configuration: %4$s"
                                  + " | Server Configuration: %5$s", getClass ().getSimpleName (), gameServerName,
                          gameServerType,
                          gameConfig, serverConfig);
  }

  @RequiredForNetworkSerialization
  private DefaultGameServerConfiguration ()
  {
    gameServerName = null;
    gameServerType = null;
    gameConfig = null;
    serverConfig = null;
  }
}
