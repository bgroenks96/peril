package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;

public interface TerritoryColorToNameConverter <T extends TerritoryColor <?>, U extends TerritoryName>
{
  public U convert (final T territoryColor);
}
