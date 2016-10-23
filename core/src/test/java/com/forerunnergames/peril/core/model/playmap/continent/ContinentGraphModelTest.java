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

package com.forerunnergames.peril.core.model.playmap.continent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import org.junit.Before;
import org.junit.Test;

public class ContinentGraphModelTest
{
  private static final int TEST_CONTINENT_COUNT = 20;
  private ImmutableSet <Continent> defaultTestContinents;

  static ContinentGraphModel createContinentGraphModelWith (final ImmutableSet <Continent> continents,
                                                            final CountryGraphModel countryGraphModel)
  {
    final DefaultGraphModel.Builder <Continent> nonConnectedGraphBuilder = DefaultGraphModel.builder ();
    for (final Continent Continent : continents)
    {
      nonConnectedGraphBuilder.addNode (Continent);
    }
    return new ContinentGraphModel (nonConnectedGraphBuilder.build (), countryGraphModel);
  }

  static ContinentGraphModel createContinentGraphModelWith (final ImmutableSet <Continent> continents)
  {
    return createContinentGraphModelWith (continents, CountryGraphModel.disjointCountryGraphFrom (CountryFactory
            .generateDefaultCountries (ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT)));
  }

  public static ContinentGraphModel createContinentGraphModelWith (final ContinentFactory continents,
                                                                   final CountryGraphModel countryGraphModel)
  {
    return createContinentGraphModelWith (continents.getContinents (), countryGraphModel);
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
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.continentPacketWith (testContinent.getId ()).is (ContinentPackets
                                                                                     .from (testContinent, ImmutableSet
                                                                                             .<CountryPacket> of ())));
    }
  }

  @Test
  public void testContinentPacketWithName ()
  {
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.continentPacketWith (testContinent.getName ())
              .is (ContinentPackets.from (testContinent, ImmutableSet.<CountryPacket> of ())));
    }
  }

  @Test
  public void testExistsContinentWithId ()
  {
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.existsContinentWith (testContinent.getId ()));
    }
  }

  @Test
  public void testExistsContinentWithName ()
  {
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    for (final Continent testContinent : defaultTestContinents)
    {
      assertTrue (modelTest.existsContinentWith (testContinent.getName ()));
    }
  }

  @Test
  public void testDoesNotExistsContinentWithName ()
  {
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    assertFalse (modelTest.existsContinentWith ("invalid-name"));
  }

  @Test
  public void testGetContinentCount ()
  {
    final ContinentGraphModel modelTest = createContinentGraphModelWith (defaultTestContinents);

    assertEquals (defaultTestContinents.size (), modelTest.getContinentCount ());
  }
}
