package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;

import com.google.common.collect.ImmutableMap;

public final class CountryColorToNameConverter extends
        AbstractTerritoryColorToNameConverter <CountryColor>
{
  public CountryColorToNameConverter (final ImmutableMap <CountryColor, String> countryColorsToNames)
  {
    super (countryColorsToNames);
  }
}
