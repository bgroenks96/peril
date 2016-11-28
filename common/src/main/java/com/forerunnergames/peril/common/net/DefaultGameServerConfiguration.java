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
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
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
  public int getTotalPlayerLimit ()
  {
    return gameConfig.getTotalPlayerLimit ();
  }

  @Override
  public int getPlayerLimitFor (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    return gameConfig.getPlayerLimitFor (sentience);
  }

  @Override
  public int getSpectatorLimit ()
  {
    return gameConfig.getSpectatorLimit ();
  }

  @Override
  public PersonLimits getPersonLimits ()
  {
    return gameConfig.getPersonLimits ();
  }

  @Override
  public int getWinPercentage ()
  {
    return gameConfig.getWinPercentage ();
  }

  @Override
  public GameRules getGameRules ()
  {
    return gameConfig.getGameRules ();
  }

  @Override
  public int getWinningCountryCount ()
  {
    return gameConfig.getWinningCountryCount ();
  }

  @Override
  public int getTotalCountryCount ()
  {
    return gameConfig.getTotalCountryCount ();
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return gameConfig.getInitialCountryAssignment ();
  }

  @Override
  public String getPlayMapName ()
  {
    return gameConfig.getPlayMapName ();
  }

  @Override
  public PlayMapMetadata getPlayMapMetadata ()
  {
    return gameConfig.getPlayMapMetadata ();
  }

  @Override
  public PlayMapType getPlayMapType ()
  {
    return gameConfig.getPlayMapType ();
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
  public String getAddress ()
  {
    return serverConfig.getAddress ();
  }

  @Override
  public int getPort ()
  {
    return serverConfig.getPort ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: GameServerName: {} | GameServerType: {} | GameConfig: {} | ServerConfig: {}",
                           getClass ().getSimpleName (), gameServerName, gameServerType, gameConfig, serverConfig);
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
