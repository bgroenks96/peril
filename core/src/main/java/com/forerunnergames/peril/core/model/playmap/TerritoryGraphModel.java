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

package com.forerunnergames.peril.core.model.playmap;

import com.forerunnergames.peril.core.model.playmap.territory.Territory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

public abstract class TerritoryGraphModel <T extends Territory> implements GraphModel <Id>
{
  private final GraphModel <T> territoryGraph;
  private final ImmutableBiMap <Id, T> territoryIdsToCountries;
  private final ImmutableMap <Id, ImmutableSet <Id>> territoryIdsToAdjacentCountries;

  protected TerritoryGraphModel (final GraphModel <T> territoryGraph)
  {
    Arguments.checkIsNotNull (territoryGraph, "territoryGraph");
    Arguments.checkHasNoNullElements (territoryGraph, "territoryGraph");

    this.territoryGraph = territoryGraph;

    // build territory id maps
    final ImmutableBiMap.Builder <Id, T> territoryIdMapBuilder = ImmutableBiMap.builder ();
    final ImmutableMap.Builder <Id, ImmutableSet <Id>> adjTerritoryMapBuilder = ImmutableMap.builder ();
    for (final T territory : territoryGraph)
    {
      territoryIdMapBuilder.put (territory.getId (), territory);

      // build adjacent territory id set
      final ImmutableSet.Builder <Id> territoryIds = ImmutableSet.builder ();
      for (final Territory adjTerritory : territoryGraph.getAdjacentNodes (territory))
      {
        territoryIds.add (adjTerritory.getId ());
      }
      adjTerritoryMapBuilder.put (territory.getId (), territoryIds.build ());
    }
    territoryIdsToCountries = territoryIdMapBuilder.build ();
    territoryIdsToAdjacentCountries = adjTerritoryMapBuilder.build ();
  }

  @Override
  public int size ()
  {
    return territoryGraph.size ();
  }

  @Override
  public boolean isEmpty ()
  {
    return territoryGraph.isEmpty ();
  }

  @Override
  public ImmutableSet <Id> getAdjacentNodes (final Id node)
  {
    Arguments.checkIsNotNull (node, "node");
    Preconditions.checkIsTrue (territoryIdsToCountries.containsKey (node),
                               Strings.format ("No territory with id [{}] exists in graph.", node));

    return territoryIdsToAdjacentCountries.get (node);
  }

  @Override
  public boolean areAdjacent (final Id node0, final Id node1)
  {
    Arguments.checkIsNotNull (node0, "node0");
    Arguments.checkIsNotNull (node1, "node1");

    return territoryGraph.areAdjacent (territoryIdsToCountries.get (node0), territoryIdsToCountries.get (node1));
  }

  @Override
  public boolean areNotAdjacent (final Id node0, final Id node1)
  {
    Arguments.checkIsNotNull (node0, "node0");
    Arguments.checkIsNotNull (node1, "node1");

    return territoryGraph.areNotAdjacent (territoryIdsToCountries.get (node0), territoryIdsToCountries.get (node1));
  }

  @Override
  public int distanceBetween (final Id node0, final Id node1)
  {
    Arguments.checkIsNotNull (node0, "node0");
    Arguments.checkIsNotNull (node1, "node1");

    return territoryGraph.distanceBetween (territoryIdsToCountries.get (node0), territoryIdsToCountries.get (node1));
  }

  @Override
  public Iterator <Id> iterator ()
  {
    return territoryIdsToCountries.keySet ().iterator ();
  }
}
