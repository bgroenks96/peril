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

package com.forerunnergames.peril.core.model.playmap.country;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class CountryNameMatcher
{
  final ImmutableSet <String> countryNames;

  public CountryNameMatcher (final ImmutableSet <String> countryNames)
  {
    Arguments.checkIsNotNull (countryNames, "countryNames");

    this.countryNames = countryNames;
  }

  public boolean countryNamesMatch (final CountryFactory factory)
  {
    final ImmutableSet <Country> countries = factory.getCountries ();

    if (countries.size () != countryNames.size ()) return false;

    for (final Country country : countries)
    {
      if (!countryNames.contains (country.getName ())) return false;
    }

    return true;
  }
}
