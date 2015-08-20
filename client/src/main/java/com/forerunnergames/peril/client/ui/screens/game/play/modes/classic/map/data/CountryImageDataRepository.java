package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class CountryImageDataRepository
{
  private final ImmutableMap <String, CountryImageData> countryNamesToImages;

  public CountryImageDataRepository (final ImmutableMap <String, CountryImageData> countryNamesToImages)
  {
    Arguments.checkIsNotNull (countryNamesToImages, "countryNamesToImages");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToImages, "countryNamesToImages");

    this.countryNamesToImages = countryNamesToImages;
  }

  public boolean has (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToImages.containsKey (countryName);
  }

  public CountryImageData get (final String name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (has (name), "Cannot find: " + name + ".");

    return countryNamesToImages.get (name);
  }

  public ImmutableSet <String> getCountryNames ()
  {
    return countryNamesToImages.keySet ();
  }
}
