package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocoordinate;

import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public interface ScreenToPlayMapCoordinateConverter
{
  Point2D convert (final Point2D screenCoordinate, final Size2D screenSize);
}
