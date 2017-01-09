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

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultGameConfiguration implements GameConfiguration
{
  private final GameMode gameMode;
  private final PlayMapMetadata playMapMetadata;
  private final GameRules gameRules;

  public DefaultGameConfiguration (final GameMode gameMode,
                                   final PlayMapMetadata playMapMetadata,
                                   final GameRules gameRules)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Arguments.checkIsNotNull (gameRules, "gameRules");

    this.gameMode = gameMode;
    this.playMapMetadata = playMapMetadata;
    this.gameRules = gameRules;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameMode;
  }

  @Override
  public int getTotalPlayerLimit ()
  {
    return gameRules.getTotalPlayerLimit ();
  }

  @Override
  public int getPlayerLimitFor (final PersonSentience sentience)
  {
    Arguments.checkIsNotNull (sentience, "sentience");

    return gameRules.getPlayerLimitFor (sentience);
  }

  @Override
  public int getSpectatorLimit ()
  {
    return gameRules.getSpectatorLimit ();
  }

  @Override
  public PersonLimits getPersonLimits ()
  {
    return gameRules.getPersonLimits ();
  }

  @Override
  public int getWinPercentage ()
  {
    return gameRules.getWinPercentage ();
  }

  @Override
  public GameRules getGameRules ()
  {
    return gameRules;
  }

  @Override
  public int getWinningCountryCount ()
  {
    return gameRules.getWinningCountryCount ();
  }

  @Override
  public int getTotalCountryCount ()
  {
    return gameRules.getTotalCountryCount ();
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return gameRules.getInitialCountryAssignment ();
  }

  @Override
  public String getPlayMapName ()
  {
    return playMapMetadata.getName ();
  }

  @Override
  public PlayMapMetadata getPlayMapMetadata ()
  {
    return playMapMetadata;
  }

  @Override
  public PlayMapType getPlayMapType ()
  {
    return playMapMetadata.getType ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: GameMode: [{}] | PersonLimits: [{}] | WinPercentage: [{}] | "
                                   + "WinningCountryCount: [{}] | TotalCountryCount: [{}] | "
                                   + "InitialCountryAssignment: [{}] | PlayMapMetadata: [{}] | GameRules: [{}]",
                           getClass ().getSimpleName (), gameMode, getPersonLimits (), getWinPercentage (),
                           getWinningCountryCount (), getTotalCountryCount (), getInitialCountryAssignment (),
                           playMapMetadata, gameRules);
  }

  @RequiredForNetworkSerialization
  private DefaultGameConfiguration ()
  {
    gameMode = null;
    playMapMetadata = null;
    gameRules = null;
  }
}
