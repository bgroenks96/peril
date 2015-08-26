package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class DefaultCountryImageDataRepository implements CountryImageDataRepository
{
  private final ImmutableMap <String, CountryImageData> countryNamesToImageData;

  public DefaultCountryImageDataRepository (final ImmutableMap <String, CountryImageData> countryNamesToImageData)
  {
    Arguments.checkIsNotNull (countryNamesToImageData, "countryNamesToImageData");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToImageData, "countryNamesToImageData");

    this.countryNamesToImageData = countryNamesToImageData;
  }

  @Override
  public boolean has (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToImageData.containsKey (countryName);
  }

  @Override
  public CountryImageData get (final String name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (has (name), "Cannot find: " + name + ".");

    return countryNamesToImageData.get (name);
  }

  @Override
  public ImmutableSet <String> getCountryNames ()
  {
    return countryNamesToImageData.keySet ();
  }
}
