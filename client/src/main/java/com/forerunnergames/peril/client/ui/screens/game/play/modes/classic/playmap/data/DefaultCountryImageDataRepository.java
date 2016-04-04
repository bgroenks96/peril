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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class DefaultCountryImageDataRepository implements CountryImageDataRepository
{
  private final ImmutableMap <String, CountryImageData> countryNamesToImageData;

  public DefaultCountryImageDataRepository (final ImmutableMap <String, CountryImageData> countryNamesToImageData)
  {
    Arguments.checkIsNotNull (countryNamesToImageData, "countryNamesToImageData");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToImageData, "countryNamesToImageData");

    this.countryNamesToImageData = countryNamesToImageData;
  }

  @Override
  public boolean has (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToImageData.containsKey (countryName);
  }

  @Override
  public CountryImageData get (final String name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (has (name), "Cannot find: " + name + ".");

    return countryNamesToImageData.get (name);
  }

  @Override
  public ImmutableSet <String> getCountryNames ()
  {
    return countryNamesToImageData.keySet ();
  }
}
