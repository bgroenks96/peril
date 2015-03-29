package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.screen;

import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public interface ScreenCoordinateToTerritoryNameConverter <T extends TerritoryName>
{
  T convert (final Point2D screenCoordinate, final Size2D screenSize);
}
