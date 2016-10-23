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

import com.forerunnergames.peril.core.model.playmap.io.CountryIdResolver;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableMap;

public final class DefaultCountryIdResolver implements CountryIdResolver
{
  private final ImmutableMap <String, Id> countryNamesToIds;

  public DefaultCountryIdResolver (final CountryFactory factory)
  {
    Arguments.checkIsNotNull (factory, "factory");

    final ImmutableMap.Builder <String, Id> countryNamesToIdsBuilder = ImmutableMap.builder ();

    for (final Country country : factory.getCountries ())
    {
      countryNamesToIdsBuilder.put (country.getName (), country.getId ());
    }

    countryNamesToIds = countryNamesToIdsBuilder.build ();
  }

  @Override
  public boolean has (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToIds.containsKey (countryName);
  }

  @Override
  public Id getIdOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesToIds.containsKey (countryName), "Country name " + "[" + countryName
            + "] does not exist.");

    return countryNamesToIds.get (countryName);
  }
}
