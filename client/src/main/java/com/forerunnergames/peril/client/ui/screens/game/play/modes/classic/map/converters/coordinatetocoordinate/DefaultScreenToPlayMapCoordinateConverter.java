package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

// @formatter:off
public final class DefaultScreenToPlayMapCoordinateConverter implements ScreenToPlayMapCoordinateConverter
{
  @Override
  public Point2D convert (final Point2D screenCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return CoordinateSpaces.actualScreenSpaceToReferencePlayMapSpace (screenCoordinate, screenSize);
  }
}
