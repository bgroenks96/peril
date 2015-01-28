package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class CountrySpriteDataRepository
{
  private final ImmutableMap <CountryName, CountrySpriteData> countryNamesToCountrySprites;

  public CountrySpriteDataRepository (final ImmutableMap <CountryName, CountrySpriteData> countryNamesToCountrySprites)
  {
    Arguments.checkIsNotNull (countryNamesToCountrySprites, "countryNamesToCountrySprites");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToCountrySprites, "countryNamesToCountrySprites");

    this.countryNamesToCountrySprites = countryNamesToCountrySprites;
  }

  public CountrySpriteData get (final CountryName name)
  {
    Arguments.checkIsNotNull (name, "name");
    Preconditions.checkIsTrue (countryNamesToCountrySprites.containsKey (name), "Cannot find: " + name + ".");

    return countryNamesToCountrySprites.get (name);
  }

  public ImmutableSet <CountryName> getCountryNames ()
  {
    return countryNamesToCountrySprites.keySet ();
  }
}
