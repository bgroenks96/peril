package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.TerritoryGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class CountryMapGraphModel extends TerritoryGraphModel <Country>
{
  private final ImmutableMap <Id, Country> countryIdsToCountries;

  CountryMapGraphModel (final GraphModel <Country> countryGraph)
  {
    super (countryGraph);

    Arguments.checkIsNotNull (countryGraph, "countryGraph");
    Arguments.checkHasNoNullElements (countryGraph, "countryGraph");

    // init country id map
    final ImmutableMap.Builder <Id, Country> countryMapBuilder = ImmutableMap.builder ();
    for (final Country country : countryGraph)
    {
      countryMapBuilder.put (country.getId (), country);
    }
    countryIdsToCountries = countryMapBuilder.build ();
  }

  public boolean existsCountryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryIdsToCountries.containsKey (countryId);
  }

  public boolean existsCountryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return findCountryByName (countryName).isPresent ();
  }

  public String nameOf (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    if (!existsCountryWith (countryId)) Exceptions.throwIllegalArg ("No country with id [{}] exists.", countryId);

    return countryIdsToCountries.get (countryId).getName ();
  }

  public Id idOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = findCountryByName (countryName);
    if (!country.isPresent ()) Exceptions.throwIllegalArg ("No country with name [{}] exists.", countryName);

    return country.get ().getId ();
  }

  public Id countryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = findCountryByName (countryName);
    if (!country.isPresent ()) Exceptions.throwIllegalArg ("No country with name [{}] exists!", countryName);

    return country.get ().getId ();
  }

  public CountryPacket countryPacketWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    if (!existsCountryWith (countryId)) Exceptions.throwIllegalArg ("No country with id [{}] exists.", countryId);

    return CountryPackets.from (countryIdsToCountries.get (countryId));
  }

  public CountryPacket countryPacketWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Optional <Country> country = findCountryByName (countryName);
    if (!country.isPresent ()) Exceptions.throwIllegalArg ("No country with name [{}] exists!", countryName);

    return CountryPackets.from (country.get ());
  }

  public ImmutableSet <CountryPacket> getCountryPackets ()
  {
    return CountryPackets.fromCountries (countryIdsToCountries.values ());
  }

  public ImmutableSet <String> getCountryNames ()
  {
    final ImmutableSet.Builder <String> builder = ImmutableSet.builder ();
    for (final Country country : countryIdsToCountries.values ())
    {
      builder.add (country.getName ());
    }
    return builder.build ();
  }

  public ImmutableSet <Id> getCountryIds ()
  {
    return countryIdsToCountries.keySet ();
  }

  public int getCountryCount ()
  {
    return countryIdsToCountries.size ();
  }

  public boolean countryCountIs (final int countryCount)
  {
    return countryCount == getCountryCount ();
  }

  public boolean countryCountIsAtLeast (final int countryCount)
  {
    return countryCount <= getCountryCount ();
  }

  Country modelCountryWith (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");

    return countryIdsToCountries.get (countryId);
  }

  Country modelCountryWith (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryIdsToCountries.get (countryName);
  }

  ImmutableSet <Country> getCountries ()
  {
    return ImmutableSet.copyOf (countryIdsToCountries.values ());
  }

  private Optional <Country> findCountryByName (final String name)
  {
    assert name != null;

    for (final Country country : countryIdsToCountries.values ())
    {
      if (country.has (name)) return Optional.of (country);
    }

    return Optional.absent ();
  }

  public static CountryMapGraphModel disjointCountryGraphFrom (final CountryFactory countries)
  {
    final DefaultGraphModel.Builder <Country> builder = DefaultGraphModel.builder ();
    for (final Country country : countries.getCountries ())
    {
      builder.addNode (country);
    }
    return new CountryMapGraphModel (builder.build ());
  }
}
