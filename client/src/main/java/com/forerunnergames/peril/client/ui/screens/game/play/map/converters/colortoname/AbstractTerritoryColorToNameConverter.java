package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractTerritoryColorToNameConverter <T extends TerritoryColor <?>, U extends TerritoryName>
                implements TerritoryColorToNameConverter <T, U>
{
  private final U UNKNOWN_TERRITORY_NAME = createTerritoryName ("");
  private final ImmutableMap <T, U> territoryColorsToNames;

  protected AbstractTerritoryColorToNameConverter (final ImmutableMap <T, U> territoryColorsToNames)
  {
    Arguments.checkIsNotNull (territoryColorsToNames, "territoryColorsToNames");

    this.territoryColorsToNames = territoryColorsToNames;
  }

  @Override
  public final U convert (final T territoryColor)
  {
    Arguments.checkIsNotNull (territoryColor, "territoryColor");

    if (!territoryColorsToNames.containsKey (territoryColor)) return UNKNOWN_TERRITORY_NAME;

    return territoryColorsToNames.get (territoryColor);
  }

  protected abstract U createTerritoryName (final String territoryNameValue);
}
