package com.forerunnergames.peril.core.model.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableBiMap;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import org.junit.BeforeClass;
import org.junit.Test;

public class ContinentModelDataLoaderTest
{
  private static final String TEST_CONTINENTS_FILENAME = "test-continents.txt";
  private static final int EXPECTED_CONTINENT_COUNT_FROM_FILE = 4;
  private static final int EXPECTED_COUNTRY_COUNT_FROM_FILE = 10;
  private static ContinentModelDataLoader loader;
  private static CountryIdResolver countryIdResolver;
  private static Collection <Matcher <? super Continent>> continentMatchers = new ArrayList <> ();

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

    loader = new ContinentModelDataLoader (countryIdResolver);

    for (int j = 1; j <= EXPECTED_CONTINENT_COUNT_FROM_FILE; ++j)
    {
      final Continent expectedContinent = mock (Continent.class);
      final String expectedContinentName = "Test Continent " + j;

      when (expectedContinent.getName ()).thenReturn (expectedContinentName);
      when (expectedContinent.getReinforcementBonus ()).thenReturn (j);
      when (expectedContinent.getCountryCount ()).thenReturn (j);

      continentMatchers.add (allOf (hasName (equalTo (expectedContinentName)), hasReinforcementBonus (equalTo (j)),
                                    hasCountryCount (equalTo (j))));
    }
  }

  @Test
  public void testLoadSuccessful ()
  {
    final ImmutableBiMap <Id, Continent> actualData = loader.load (TEST_CONTINENTS_FILENAME);

    assertThat (actualData.values (), containsInAnyOrder (continentMatchers));
  }

  @Test (expected = RuntimeException.class)
  public void testLoadFailsFileNotFound ()
  {
    loader.load ("non-existent-file");
  }

  private static Matcher <Continent> hasName (final Matcher <String> nameMatcher)
  {
    return new FeatureMatcher <Continent, String> (nameMatcher, "Continent with name", "name")
    {
      @Override
      protected String featureValueOf (final Continent actual)
      {
        return actual.getName ();
      }
    };
  }

  private static Matcher <Continent> hasReinforcementBonus (final Matcher <Integer> reinforcementBonusMatcher)
  {
    return new FeatureMatcher <Continent, Integer> (reinforcementBonusMatcher, "Continent with reinforcement bonus",
            "reinforcement bonus")
    {
      @Override
      protected Integer featureValueOf (final Continent actual)
      {
        return actual.getReinforcementBonus ();
      }
    };
  }

  private static Matcher <Continent> hasCountryCount (final Matcher <Integer> countryCountMatcher)
  {
    return new FeatureMatcher <Continent, Integer> (countryCountMatcher, "Continent with country count",
            "country count")
    {
      @Override
      protected Integer featureValueOf (final Continent actual)
      {
        return actual.getCountryCount ();
      }
    };
  }
}
