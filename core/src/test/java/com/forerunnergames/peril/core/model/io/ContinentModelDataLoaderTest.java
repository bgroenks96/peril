package com.forerunnergames.peril.core.model.io;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.io.DataLoader;
import com.forerunnergames.peril.core.model.map.continent.ContinentDataMatcher;
import com.forerunnergames.peril.core.model.map.continent.ContinentDataMatcher.ContinentData;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
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
}
