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

package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;

public final class CountryFactory
{
  private final ImmutableSet.Builder <Country> countries = ImmutableSet.builder ();

  private int countryCount = 0;

  static CountryBuilder builder (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return new CountryBuilder (name);
  }

  static Country create (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return builder (name).build ();
  }

  static Country create (final String name, final int armyCount)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    return builder (name).armies (armyCount).build ();
  }

  public static CountryFactory generateDefaultCountries (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    final CountryFactory countryFactory = new CountryFactory ();
    for (int i = 0; i < count; ++i)
    {
      countryFactory.newCountryWith ("Country-" + i);
    }
    return countryFactory;
  }

  public void newCountryWith (final String name)
  {
    countries.add (create (name));
    countryCount++;
  }

  public void newCountryWith (final String name, final int armyCount)
  {
    countries.add (create (name, armyCount));
    countryCount++;
  }

  public int getCountryCount ()
  {
    return countryCount;
  }

  ImmutableSet <Country> getCountries ()
  {
    return countries.build ();
  }

  static class CountryBuilder
  {
    private final String name;
    private final Id id;
    private int armyCount = 0;

    public CountryBuilder (final String countryName)
    {
      Arguments.checkIsNotNull (countryName, "countryName");

      name = countryName;
      id = IdGenerator.generateUniqueId ();
    }

    public CountryBuilder armies (final int armyCount)
    {
      Arguments.checkIsNotNegative (armyCount, "armyCount");

      this.armyCount = armyCount;
      return this;
    }

    public Country build ()
    {
      return new DefaultCountry (name, id, armyCount);
    }
  }
}
