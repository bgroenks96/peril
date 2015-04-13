package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface TerritoryColorToNameConverter <T extends TerritoryColor <?>, U extends TerritoryName>
{
  U convert (final T territoryColor);
}