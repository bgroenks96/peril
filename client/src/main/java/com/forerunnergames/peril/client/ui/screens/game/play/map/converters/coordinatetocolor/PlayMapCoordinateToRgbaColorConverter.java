package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocolor;

import com.forerunnergames.tools.common.color.RgbaColor;
import com.forerunnergames.tools.common.geometry.Point2D;

public interface PlayMapCoordinateToRgbaColorConverter
{
  RgbaColor convert (final Point2D playMapCoordinate);
}
