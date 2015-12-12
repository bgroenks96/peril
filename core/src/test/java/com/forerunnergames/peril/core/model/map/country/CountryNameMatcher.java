package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class CountryNameMatcher
{
  final ImmutableSet <String> countryNames;

  public CountryNameMatcher (final ImmutableSet <String> countryNames)
  {
    Arguments.checkIsNotNull (countryNames, "countryNames");

    this.countryNames = countryNames;
  }

  public boolean countryNamesMatch (final CountryFactory factory)
  {
    final ImmutableSet <Country> countries = factory.getCountries ();

    if (countries.size () != countryNames.size ()) return false;

    for (final Country country : countries)
    {
      if (!countryNames.contains (country.getName ())) return false;
    }

    return true;
  }
}
