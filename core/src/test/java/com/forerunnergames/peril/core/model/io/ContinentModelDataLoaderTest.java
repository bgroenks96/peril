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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.io.DataLoader;
import com.forerunnergames.peril.core.model.map.continent.ContinentDataMatcher;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentDataMatcher.ContinentData;
import com.forerunnergames.peril.core.model.map.io.ContinentModelDataLoader;
import com.forerunnergames.peril.core.model.map.io.CountryIdResolver;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;

import org.junit.BeforeClass;

public class ContinentModelDataLoaderTest extends DataLoaderTest <ContinentFactory>
{
  private static final String TEST_CONTINENTS_FILENAME = "test-continents.txt";
  private static final int EXPECTED_CONTINENT_COUNT_FROM_FILE = 4;
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static CountryIdResolver countryIdResolver;
  private static ContinentDataMatcher continentDataMatcher;

  @Override
  public DataLoader <ContinentFactory> createDataLoader ()
  {
    return new ContinentModelDataLoader (new InternalStreamParserFactory (), countryIdResolver);
  }

  @Override
  public boolean verifyData (final ContinentFactory data)
  {
    return continentDataMatcher.continentDataMatch (data);
  }

  @Override
  public String getTestDataFileName ()
  {
    return TEST_CONTINENTS_FILENAME;
  }

  @BeforeClass
  public static void setupClass ()
  {
    countryIdResolver = mock (CountryIdResolver.class);

    for (int i = 1; i <= EXPECTED_COUNTRY_COUNT_FROM_FILE; ++i)
    {
      final String expectedCountryName = "Test Country " + i;
      when (countryIdResolver.has (expectedCountryName)).thenReturn (true);
      when (countryIdResolver.getIdOf (expectedCountryName)).thenReturn (IdGenerator.generateUniqueId ());
    }

    final ImmutableSet.Builder <ContinentData> continentDataBuilder = ImmutableSet.builder ();
    for (int j = 1; j <= EXPECTED_CONTINENT_COUNT_FROM_FILE; ++j)
    {
      final String expectedContinentName = "Test Continent " + j;

      continentDataBuilder.add (new ContinentData (expectedContinentName, j, j));
    }
    continentDataMatcher = new ContinentDataMatcher (continentDataBuilder.build ());
  }
}
