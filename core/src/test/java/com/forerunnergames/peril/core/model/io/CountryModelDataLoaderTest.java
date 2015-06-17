package com.forerunnergames.peril.core.model.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableBiMap;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import org.junit.BeforeClass;
import org.junit.Test;

public class CountryModelDataLoaderTest
{
  private static final String TEST_COUNTRIES_FILENAME = "test-countries.txt";
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static CountryModelDataLoader loader;
  private static Collection <Matcher <? super Country>> countryMatchers = new ArrayList <> ();

  @BeforeClass
  public static void setupClass ()
  {
    loader = new CountryModelDataLoader ();

    for (int i = 1; i <= EXPECTED_COUNTRY_COUNT_FROM_FILE; ++i)
    {
      final Country expectedCountry = mock (Country.class);
      final String expectedCountryName = "Test Country " + i;

      when (expectedCountry.getName ()).thenReturn (expectedCountryName);

      countryMatchers.add (hasName (equalTo (expectedCountryName)));
    }
  }

  @Test
  public void testLoadSuccessful ()
  {
    final ImmutableBiMap <Id, Country> actualData = loader.load (TEST_COUNTRIES_FILENAME);

    assertThat (actualData.values (), containsInAnyOrder (countryMatchers));
  }

  @Test (expected = RuntimeException.class)
  public void testLoadFailsFileNotFound ()
  {
    loader.load ("non-existent-file");
  }

  private static Matcher <Country> hasName (final Matcher <String> nameMatcher)
  {
    return new FeatureMatcher <Country, String> (nameMatcher, "Country with name", "name")
    {
      @Override
      protected String featureValueOf (final Country actual)
      {
        return actual.getName ();
      }
    };
  }
}
