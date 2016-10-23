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

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.playmap.TerritoryGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class CountryGraphModel extends TerritoryGraphModel <Country>
{
  private final ImmutableMap <Id, Country> countryIdsToCountries;

  CountryGraphModel (final GraphModel <Country> countryGraph)
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

  public static CountryGraphModel disjointCountryGraphFrom (final CountryFactory countries)
  {
    final DefaultGraphModel.Builder <Country> builder = DefaultGraphModel.builder ();
    for (final Country country : countries.getCountries ())
    {
      builder.addNode (country);
    }
    return new CountryGraphModel (builder.build ());
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

    return countryIdsToCountries.get (idOf (countryName));
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
}
