package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.TerritoryGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class ContinentMapGraphModel extends TerritoryGraphModel <Continent>
{
  private final ImmutableMap <Id, Continent> continentIdsToContinents;
  private final ImmutableMap <Id, Id> countryIdsToContinentIds;
  private final CountryMapGraphModel countryMapGraphModel;

  ContinentMapGraphModel (final GraphModel <Continent> continentGraph, final CountryMapGraphModel countryMapGraphModel)
  {
    super (continentGraph);

    Arguments.checkIsNotNull (countryMapGraphModel, "countryMapGraphModel");

    final ImmutableMap.Builder <Id, Continent> continentIdMapBuilder = ImmutableMap.builder ();
    final ImmutableMap.Builder <Id, Id> countryMapBuilder = ImmutableMap.builder ();
    for (final Continent continent : continentGraph)
    {
      continentIdMapBuilder.put (continent.getId (), continent);

      for (final Id id : continent.getCountryIds ())
      {
        countryMapBuilder.put (id, continent.getId ());
      }
    }
    continentIdsToContinents = continentIdMapBuilder.build ();
    countryIdsToContinentIds = countryMapBuilder.build ();

    this.countryMapGraphModel = countryMapGraphModel;
  }

  public boolean existsContinentWith (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");

    return continentIdsToContinents.containsKey (continentId);
  }

  public boolean existsContinentWith (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    return findContinentByName (continentName).isPresent ();
  }

  public boolean isCountryIn (final Id countryId, final Id continentId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNull (continentId, "continentId");

    final Continent continent = continentOf (countryId);

    return continent.getId ().is (continentId);
  }

  public ContinentPacket continentPacketWith (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");

    return ContinentPackets.from (continentWith (continentId), makeCountryPacketsFor (continentId));
  }

  public ContinentPacket continentPacketWith (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    final Continent continent = continentWith (continentName);

    return ContinentPackets.from (continent, makeCountryPacketsFor (continent.getId ()));
  }

  public ContinentPacket continentPacketOf (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    final Continent continent = continentOf (countryId);

    return ContinentPackets.from (continent, makeCountryPacketsFor (continent.getId ()));
  }

  public ImmutableSet <ContinentPacket> getContinentPackets ()
  {
    final ImmutableSet.Builder <ContinentPacket> continentSetBuilder = ImmutableSet.builder ();
    for (final Id continentId : continentIdsToContinents.keySet ())
    {
      continentSetBuilder.add (continentPacketWith (continentId));
    }
    return continentSetBuilder.build ();
  }

  public ImmutableSet <CountryPacket> getCountriesIn (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");
    Preconditions.checkIsTrue (existsContinentWith (continentId),
                               Strings.format ("No continent with id [{}] exists.", continentId));

    return makeCountryPacketsFor (continentId);
  }

  public int getContinentCount ()
  {
    return continentIdsToContinents.size ();
  }

  Continent continentWith (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");

    return continentIdsToContinents.get (continentId);
  }

  Continent continentWith (final String continentName)
  {
    Arguments.checkIsNotNull (continentName, "continentName");

    final Optional <Continent> res = findContinentByName (continentName);
    if (!res.isPresent ()) Exceptions.throwIllegalArg ("No continent with name [{}] exists.", continentName);

    return res.get ();
  }

  Continent continentOf (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Preconditions.checkIsTrue (countryMapGraphModel.existsCountryWith (countryId),
                               Strings.format ("No country with id [{}] exists.", countryId));
    Preconditions.checkIsTrue (countryIdsToContinentIds.containsKey (countryId),
                               Strings.format ("No continent assigned to country with id [{}].", countryId));

    return continentWith (countryIdsToContinentIds.get (countryId));
  }

  ImmutableSet <Continent> getContinents ()
  {
    return ImmutableSet.copyOf (continentIdsToContinents.values ());
  }

  private Optional <Continent> findContinentByName (final String name)
  {
    assert name != null;

    for (final Continent cont : continentIdsToContinents.values ())
    {
      if (cont.has (name)) return Optional.of (cont);
    }

    return Optional.absent ();
  }

  private ImmutableSet <CountryPacket> makeCountryPacketsFor (final Id continentId)
  {
    final Continent continent = continentWith (continentId);
    final ImmutableSet.Builder <CountryPacket> countrySetBuilder = ImmutableSet.builder ();
    for (final Id countryId : continent.getCountryIds ())
    {
      countrySetBuilder.add (countryMapGraphModel.countryPacketWith (countryId));
    }
    return countrySetBuilder.build ();
  }

  public static ContinentMapGraphModel disjointContinentGraphFrom (final ContinentFactory continents,
                                                                   final CountryMapGraphModel countryMapGraphModel)
  {
    final DefaultGraphModel.Builder <Continent> builder = DefaultGraphModel.builder ();
    for (final Continent continent : continents.getContinents ())
    {
      builder.addNode (continent);
    }
    return new ContinentMapGraphModel (builder.build (), countryMapGraphModel);
  }
}
