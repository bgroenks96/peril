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

package com.forerunnergames.peril.desktop.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Strings;

public final class ClanAcronymParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!GameSettings.isValidHumanClanAcronym (value))
    {
      throw new ParameterException (
              Strings.format ("Invalid value \"{}\" for parameter \"{}\".\n\nValid clan acronym rules:\n\n{}", value,
                              name, GameSettings.VALID_CLAN_ACRONYM_DESCRIPTION));
    }
  }
}
