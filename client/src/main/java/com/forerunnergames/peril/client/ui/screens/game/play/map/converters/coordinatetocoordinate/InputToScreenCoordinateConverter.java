package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocoordinate;

import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public interface InputToScreenCoordinateConverter
{
  public Point2D convert (final Point2D inputCoordinate, final Size2D screenSize);
}
