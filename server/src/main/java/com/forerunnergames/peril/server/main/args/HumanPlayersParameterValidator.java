/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Strings;

public final class HumanPlayersParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    try
    {
      final int humanPlayers = Integer.valueOf (value);

      if (humanPlayers < GameSettings.MIN_HUMAN_PLAYERS || humanPlayers > ClassicGameRules.MAX_PLAYERS)
      {
        throw new ParameterException (createErrorMessage (name, value));
      }
    }
    catch (final NumberFormatException e)
    {
      throw new ParameterException (createErrorMessage (name, value), e);
    }
  }

  private String createErrorMessage (final String name, final String value)
  {
    // @formatter:off
    return Strings.format ("Invalid value \"{}\" for parameter \"{}\".\n\n" +
                           "Valid values:" +
                           "\n\n" +
                           "  Humans: {} - {}\n" +
                           "  AI:     {} - {}\n" +
                           "  Total:  {} - {}",
            value, name,
            GameSettings.MIN_HUMAN_PLAYERS, ClassicGameRules.MAX_PLAYERS,
            GameSettings.MIN_AI_PLAYERS, ClassicGameRules.MAX_PLAYERS,
            ClassicGameRules.MIN_PLAYERS, ClassicGameRules.MAX_PLAYERS);
    // @formatter:on
  }
}
