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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.tools.common.graph.DefaultGraph;
import com.forerunnergames.tools.common.graph.Graph;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class CountryGraphModelTest
{
  private static final int TEST_COUNTRY_COUNT = 20;
  private ImmutableSet <Country> defaultTestCountries;

  static CountryGraphModel createDisjointCountryGraphModelWith (final ImmutableSet <Country> countries)
  {
    final DefaultGraph.Builder <Country> nonConnectedGraphBuilder = DefaultGraph.builder ();
    for (final Country country : countries)
    {
      nonConnectedGraphBuilder.addNode (country);
    }
    return new CountryGraphModel (nonConnectedGraphBuilder.build ());
  }

  public static CountryGraphModel createDisjointCountryGraphModelWith (final CountryFactory countries)
  {
    return createDisjointCountryGraphModelWith (countries.getCountries ());
  }

  public static CountryGraphModel createCountryGraphModelFrom (final Graph <String> countryNameGraph)
  {
    final DefaultGraph.Builder <Country> graphBuilder = DefaultGraph.builder ();
    final Map <String, Country> namesToCountries = Maps.newHashMap ();
    for (final String node : countryNameGraph)
    {
      namesToCountries.put (node, CountryFactory.create (node));
    }
    for (final String node : countryNameGraph)
    {
      final Country countryNode = namesToCountries.get (node);
      for (final String adj : countryNameGraph.getAdjacentNodes (node))
      {
        final Country adjCountryNode = namesToCountries.get (adj);
        graphBuilder.setAdjacent (countryNode, adjCountryNode);
      }
    }
    return new CountryGraphModel (graphBuilder.build ());
  }

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
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryPacketWith (testCountry.getId ()).is (CountryPackets.from (testCountry)));
    }
  }

  @Test
  public void testCountryPacketWithName ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.countryPacketWith (testCountry.getName ()).is (CountryPackets.from (testCountry)));
    }
  }

  @Test
  public void testExistsCountryWithId ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (testCountry.getId ()));
    }
  }

  @Test
  public void testExistsCountryWithName ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    for (final Country testCountry : defaultTestCountries)
    {
      assertTrue (modelTest.existsCountryWith (testCountry.getName ()));
    }
  }

  @Test
  public void testDoesNotExistsCountryWithName ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.existsCountryWith ("invalid-name"));
  }

  @Test
  public void testGetCountryCount ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertEquals (defaultTestCountries.size (), modelTest.getCountryCount ());
  }

  @Test
  public void testGetCountryCountIsSizeOfDefault ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIs (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsFalseOnCountPlusOne ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIs (defaultTestCountries.size () + 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefault ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size ()));
  }

  @Test
  public void testGetCountryCountIsAtLeastSizeOfDefaultMinusOne ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertTrue (modelTest.countryCountIsAtLeast (defaultTestCountries.size () - 1));
  }

  @Test
  public void testGetCountryCountIsAtLeastFalseForSizeOfDefaultPlusOne ()
  {
    final CountryGraphModel modelTest = createDisjointCountryGraphModelWith (defaultTestCountries);

    assertFalse (modelTest.countryCountIsAtLeast (defaultTestCountries.size () + 1));
  }
}
