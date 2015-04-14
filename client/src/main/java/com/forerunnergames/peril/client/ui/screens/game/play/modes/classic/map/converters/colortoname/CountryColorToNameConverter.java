package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public final class CountryColorToNameConverter extends
        AbstractTerritoryColorToNameConverter <CountryColor, CountryName>
{
  public CountryColorToNameConverter (final ImmutableMap <CountryColor, CountryName> countryColorsToNames)
  {
    super (countryColorsToNames);
  }

  @Override
  protected CountryName createTerritoryName (final String territoryNameValue)
  {
    Arguments.checkIsNotNull (territoryNameValue, "territoryNameValue");

    return new CountryName (territoryNameValue);
  }
}
