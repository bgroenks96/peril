package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import com.google.common.collect.HashBasedTable;

public class DefaultInputToScreenCoordinateConverter implements InputToScreenCoordinateConverter
{
  private final HashBasedTable <Point2D, Size2D, Point2D> inputToScreenCoordinates = HashBasedTable.create ();
  private Point2D screenCoordinate;

  @Override
  public Point2D convert (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    screenCoordinate = inputToScreenCoordinates.get (inputCoordinate, screenSize);

    if (screenCoordinate != null) return screenCoordinate;

    screenCoordinate = CoordinateSpaces.actualInputSpaceToActualScreenSpace (inputCoordinate, screenSize);

    inputToScreenCoordinates.put (inputCoordinate, screenSize, screenCoordinate);

    return screenCoordinate;
  }
}
