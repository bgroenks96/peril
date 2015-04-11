package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class CountryImageDataRepository
{
  private final ImmutableMap <CountryName, CountryImageData> countryNamesToImages;

  public CountryImageDataRepository (final ImmutableMap <CountryName, CountryImageData> countryNamesToImages)
  {
    Arguments.checkIsNotNull (countryNamesToImages, "countryNamesToImages");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToImages, "countryNamesToImages");

    this.countryNamesToImages = countryNamesToImages;
  }

  public boolean has (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToImages.containsKey (countryName);
  }

  public boolean doesNotHave (final CountryName countryName)
  {
    return !has (countryName);
  }

  public CountryImageData get (final CountryName name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (has (name), "Cannot find: " + name + ".");

    return countryNamesToImages.get (name);
  }

  public ImmutableSet <CountryName> getCountryNames ()
  {
    return countryNamesToImages.keySet ();
  }
}
