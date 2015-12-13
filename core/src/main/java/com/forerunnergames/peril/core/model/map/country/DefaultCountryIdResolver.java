package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.core.model.map.io.CountryIdResolver;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableMap;

public final class DefaultCountryIdResolver implements CountryIdResolver
{
  private final ImmutableMap <String, Id> countryNamesToIds;

  public DefaultCountryIdResolver (final CountryFactory factory)
  {
    Arguments.checkIsNotNull (factory, "factory");

    final ImmutableMap.Builder <String, Id> countryNamesToIdsBuilder = ImmutableMap.builder ();

    for (final Country country : factory.getCountries ())
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
