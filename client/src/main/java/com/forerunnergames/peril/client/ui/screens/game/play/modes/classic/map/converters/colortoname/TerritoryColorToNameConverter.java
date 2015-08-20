package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;

public interface TerritoryColorToNameConverter <T extends TerritoryColor <?>>
{
  String convert (final T territoryColor);
}
