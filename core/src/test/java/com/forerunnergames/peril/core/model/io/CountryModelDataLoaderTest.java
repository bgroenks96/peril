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

package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.common.io.DataLoader;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryNameMatcher;
import com.forerunnergames.peril.core.model.playmap.io.CountryModelDataLoader;

import com.google.common.collect.ImmutableSet;

import org.junit.BeforeClass;

public class CountryModelDataLoaderTest extends DataLoaderTest <CountryFactory>
{
  private static final String TEST_COUNTRIES_FILENAME = "test-countries.txt";
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static ImmutableSet <String> expectedCountryNames;
  private static CountryNameMatcher countryNameMatcher;

  @Override
  public DataLoader <CountryFactory> createDataLoader ()
  {
    return new CountryModelDataLoader (new InternalStreamParserFactory ());
  }

  @Override
  public boolean verifyData (final CountryFactory data)
  {
    return countryNameMatcher.countryNamesMatch (data);
  }

  @Override
  public String getTestDataFileName ()
  {
    return TEST_COUNTRIES_FILENAME;
  }

  @BeforeClass
  public static void setupClass ()
  {
    final ImmutableSet.Builder <String> countryNames = ImmutableSet.builder ();
    for (int i = 1; i <= EXPECTED_COUNTRY_COUNT_FROM_FILE; ++i)
    {
      final String expectedCountryName = "Test Country " + i;

      countryNames.add (expectedCountryName);
    }

    expectedCountryNames = countryNames.build ();
    countryNameMatcher = new CountryNameMatcher (expectedCountryNames);
  }
}
