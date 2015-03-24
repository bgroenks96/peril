package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

// @formatter:off
public final class ContinentColorToNameConverter extends AbstractTerritoryColorToNameConverter <ContinentColor, ContinentName>
{
  public ContinentColorToNameConverter (final ImmutableMap <ContinentColor, ContinentName> continentColorsToNames)
  {
    super (continentColorsToNames);
  }

  @Override
  protected ContinentName createTerritoryName (final String territoryNameValue)
  {
    Arguments.checkIsNotNull (territoryNameValue, "territoryNameValue");

    return new ContinentName (territoryNameValue);
  }
}
