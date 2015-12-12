package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.common.io.DataLoader;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryNameMatcher;
import com.forerunnergames.peril.core.model.map.io.CountryModelDataLoader;

import com.google.common.collect.ImmutableSet;

import org.junit.BeforeClass;

public class CountryModelDataLoaderTest extends DataLoaderTest <CountryFactory>
{
  private static final String TEST_COUNTRIES_FILENAME = "test-countries.txt";
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static ImmutableSet <String> expectedCountryNames;
  private static CountryNameMatcher countryNameMatcher;

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
}
