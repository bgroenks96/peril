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

package com.forerunnergames.peril.common.game.rules;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import javax.annotation.Nullable;

public final class GameRulesFactory
{
  public static GameRules create (final GameMode gameMode,
                                  final PersonLimits personLimits,
                                  @Nullable final Integer winPercentage,
                                  @Nullable final Integer totalCountryCount,
                                  @Nullable final InitialCountryAssignment initialCountryAssignment)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (personLimits, "personLimits");

    switch (gameMode)
    {
      case CLASSIC:
      {
        return ClassicGameRules.builder ().personLimits (personLimits).winPercentage (winPercentage)
                .totalCountryCount (totalCountryCount).initialCountryAssignment (initialCountryAssignment).build ();
      }
      case CUSTOM:
      {
        throw new UnsupportedOperationException ("Custom game mode is not supported yet!");
      }
      default:
      {
        throw new IllegalStateException ("Unknown game mode");
      }
    }
  }

  private GameRulesFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
