package com.forerunnergames.peril.core.model.map.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.tools.common.graph.DefaultGraphModel;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public class CountryMapGraphModelTest
{
  private static final int TEST_COUNTRY_COUNT = 20;
  private ImmutableSet <Country> defaultTestCountries;

  @Before
  public void init ()
  {
    final CountryFactory countryFactory = new CountryFactory ();
    for (int i = 0; i < TEST_COUNTRY_COUNT; i++)
    {
      countryFactory.newCountryWith ("TestCountry-" + i);
    }
    defaultTestCountries = countryFactory.getCountries ();
  }

  @Test
  public void testCountryPacketWithId ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryPacketWith (testCountry.getId ()).is (CountryPackets.from (testCountry)));
    }
  }

  @Test
  public void testCountryPacketWithName ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryPacketWith (testCountry.getName ()).is (CountryPackets.from (testCountry)));
    }
  }

  @Test
  public void testExistsCountryWithId ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (testCountry.getId ()));
    }
  }

  @Test
  public void testExistsCountryWithName ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (testCountry.getName ()));
    }
  }

  @Test
  public void testDoesNotExistsCountryWithName ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.existsCountryWith ("invalid-name"));
  }

  @Test
  public void testGetCountryCount ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertEquals (defaultTestCountries.size (), modelTest.getCountryCount ());
  }

  @Test
  public void testGetCountryCountIsSizeOfDefault ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIs (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsFalseOnCountPlusOne ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIs (defaultTestCountries.size () + 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefault ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefaultMinusOne ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size () - 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastFalseForSizeOfDefaultPlusOne ()
  {
    final CountryMapGraphModel modelTest = createCountryMapGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIsAtLeast (defaultTestCountries.size () + 1));
  }

  public static CountryMapGraphModel createCountryMapGraphModelWith (final CountryFactory countries)
  {
    return createCountryMapGraphModelWith (countries.getCountries ());
  }

  static CountryMapGraphModel createCountryMapGraphModelWith (final ImmutableSet <Country> countries)
  {
    final DefaultGraphModel.Builder <Country> nonConnectedGraphBuilder = DefaultGraphModel.builder ();
    for (final Country country : countries)
    {
      nonConnectedGraphBuilder.addNode (country);
    }
    return new CountryMapGraphModel (nonConnectedGraphBuilder.build ());
  }
}
