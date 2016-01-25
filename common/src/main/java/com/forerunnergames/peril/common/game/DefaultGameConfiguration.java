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

package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultGameConfiguration implements GameConfiguration
{
  private final GameMode gameMode;
  private final int playerLimit;
  private final int spectatorLimit;
  private final int winPercentage;
  private final InitialCountryAssignment initialCountryAssignment;
  private final MapMetadata mapMetadata;

  public DefaultGameConfiguration (final GameMode gameMode,
                                   final int playerLimit,
                                   final int spectatorLimit,
                                   final int winPercentage,
                                   final InitialCountryAssignment initialCountryAssignment,
                                   final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");
    Arguments.checkIsNotNegative (spectatorLimit, "spectatorLimit");
    Arguments.checkIsNotNegative (winPercentage, "winPercentage");
    Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    this.gameMode = gameMode;
    this.playerLimit = playerLimit;
    this.spectatorLimit = spectatorLimit;
    this.winPercentage = winPercentage;
    this.initialCountryAssignment = initialCountryAssignment;
    this.mapMetadata = mapMetadata;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameMode;
  }

  @Override
  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  @Override
  public int getSpectatorLimit ()
  {
    return spectatorLimit;
  }

  @Override
  public int getWinPercentage ()
  {
    return winPercentage;
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return initialCountryAssignment;
  }

  @Override
  public String getMapName ()
  {
    return mapMetadata.getName ();
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return mapMetadata;
  }

  @Override
  public MapType getMapType ()
  {
    return mapMetadata.getType ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Game mode: {} | Player limit: {} | Spectator Limit: {} | Win Percentage: {}"
                                   + " | Initial Country Assignment: {} | Map: {}",
                           getClass ().getSimpleName (), gameMode, playerLimit, spectatorLimit, winPercentage,
                           initialCountryAssignment, mapMetadata);
  }

  @RequiredForNetworkSerialization
  private DefaultGameConfiguration ()
  {
    gameMode = null;
    playerLimit = 0;
    spectatorLimit = 0;
    winPercentage = 0;
    initialCountryAssignment = null;
    mapMetadata = null;
  }
}
