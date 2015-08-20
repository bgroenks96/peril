package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;

import com.google.common.collect.ImmutableMap;

public final class ContinentColorToNameConverter extends AbstractTerritoryColorToNameConverter <ContinentColor>
{
  public ContinentColorToNameConverter (final ImmutableMap <ContinentColor, String> continentColorsToNames)
  {
    super (continentColorsToNames);
  }
}
