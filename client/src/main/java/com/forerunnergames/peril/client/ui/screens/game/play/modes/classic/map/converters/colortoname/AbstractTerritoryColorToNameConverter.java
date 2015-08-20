package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractTerritoryColorToNameConverter <T extends TerritoryColor <?>>
        implements TerritoryColorToNameConverter <T>
{
  private static final String UNKNOWN_TERRITORY_NAME = "";
  private final ImmutableMap <T, String> territoryColorsToNames;

  protected AbstractTerritoryColorToNameConverter (final ImmutableMap <T, String> territoryColorsToNames)
  {
    Arguments.checkIsNotNull (territoryColorsToNames, "territoryColorsToNames");

    this.territoryColorsToNames = territoryColorsToNames;
  }

  @Override
  public final String convert (final T territoryColor)
  {
    Arguments.checkIsNotNull (territoryColor, "territoryColor");

    if (!territoryColorsToNames.containsKey (territoryColor)) return UNKNOWN_TERRITORY_NAME;

    return territoryColorsToNames.get (territoryColor);
  }
}
