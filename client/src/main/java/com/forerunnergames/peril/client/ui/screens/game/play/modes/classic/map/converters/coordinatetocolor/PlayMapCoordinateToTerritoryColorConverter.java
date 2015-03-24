package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.tools.common.geometry.Point2D;

public interface PlayMapCoordinateToTerritoryColorConverter <T extends TerritoryColor <?>>
{
  public T convert (final Point2D playMapCoordinate);
}
