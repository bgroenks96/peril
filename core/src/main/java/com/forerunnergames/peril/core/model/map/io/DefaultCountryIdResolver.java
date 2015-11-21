package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class DefaultCountryIdResolver implements CountryIdResolver
{
  private final ImmutableMap <String, Id> countryNamesToIds;

  public DefaultCountryIdResolver (final ImmutableSet <Country> countries)
  {
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    final ImmutableMap.Builder <String, Id> countryNamesToIdsBuilder = ImmutableMap.builder ();

    for (final Country country : countries)
    {
      countryNamesToIdsBuilder.put (country.getName (), country.getId ());
    }

    countryNamesToIds = countryNamesToIdsBuilder.build ();
  }

  @Override
  public boolean has (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToIds.containsKey (countryName);
  }

  @Override
  public Id getIdOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesToIds.containsKey (countryName),
                               "Country name " + "[" + countryName + "] does not exist.");

    return countryNamesToIds.get (countryName);
  }
}
