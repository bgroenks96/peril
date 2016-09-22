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

package com.forerunnergames.peril.core.model.map.continent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public class ContinentMapGraphModelTest
{
  private static final int TEST_CONTINENT_COUNT = 20;
  private ImmutableSet <Continent> defaultTestContinents;

  static ContinentMapGraphModel createContinentMapGraphModelWith (final ImmutableSet <Continent> continents,
                                                                  final CountryMapGraphModel countryMapGraphModel)
  {
    final DefaultGraphModel.Builder <Continent> nonConnectedGraphBuilder = DefaultGraphModel.builder ();
    for (final Continent Continent : continents)
    {
      nonConnectedGraphBuilder.addNode (Continent);
    }
    return new ContinentMapGraphModel (nonConnectedGraphBuilder.build (), countryMapGraphModel);
  }

  static ContinentMapGraphModel createContinentMapGraphModelWith (final ImmutableSet <Continent> continents)
  {
    return createContinentMapGraphModelWith (continents, CountryMapGraphModel.disjointCountryGraphFrom (CountryFactory
            .generateDefaultCountries (ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT)));
  }

  public static ContinentMapGraphModel createContinentMapGraphModelWith (final ContinentFactory continents,
                                                                         final CountryMapGraphModel countryMapGraphModel)
  {
    return createContinentMapGraphModelWith (continents.getContinents (), countryMapGraphModel);
  }

  @Before
  public void init ()
  {
    final ContinentFactory continentFactory = new ContinentFactory ();
    for (int i = 0; i < TEST_CONTINENT_COUNT; i++)
    {
      continentFactory.newContinentWith ("TestContinent-" + i, ImmutableSet.<Id> of ());
    }
    defaultTestContinents = continentFactory.getContinents ();
  }

  @Test
  public void testContinentPacketWithId ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.continentPacketWith (testContinent.getId ())
              .is (ContinentPackets.from (testContinent, ImmutableSet.<CountryPacket> of ())));
    }
  }

  @Test
  public void testContinentPacketWithName ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.continentPacketWith (testContinent.getName ())
              .is (ContinentPackets.from (testContinent, ImmutableSet.<CountryPacket> of ())));
    }
  }

  @Test
  public void testExistsContinentWithId ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.existsContinentWith (testContinent.getId ()));
    }
  }

  @Test
  public void testExistsContinentWithName ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.existsContinentWith (testContinent.getName ()));
    }
  }

  @Test
  public void testDoesNotExistsContinentWithName ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    assertFalse (modelTest.existsContinentWith ("invalid-name"));
  }

  @Test
  public void testGetContinentCount ()
  {
    final ContinentMapGraphModel modelTest = createContinentMapGraphModelWith (defaultTestContinents);

    assertEquals (defaultTestContinents.size (), modelTest.getContinentCount ());
  }
}
