package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToTerritoryNameConverter;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultPlayMapInputDetection implements PlayMapInputDetection
{
  private final InputCoordinateToTerritoryNameConverter inputCoordinateToCountryNameConverter;
  private final InputCoordinateToTerritoryNameConverter inputCoordinateToContinentNameConverter;

  public DefaultPlayMapInputDetection (final InputCoordinateToTerritoryNameConverter inputCoordinateToCountryNameConverter,
                                       final InputCoordinateToTerritoryNameConverter inputCoordinateToContinentNameConverter)
  {
    Arguments.checkIsNotNull (inputCoordinateToCountryNameConverter, "inputCoordinateToCountryNameConverter");
    Arguments.checkIsNotNull (inputCoordinateToContinentNameConverter, "inputCoordinateToContinentNameConverter");

    this.inputCoordinateToCountryNameConverter = inputCoordinateToCountryNameConverter;
    this.inputCoordinateToContinentNameConverter = inputCoordinateToContinentNameConverter;
  }

  @Override
  public String getCountryNameAt (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return inputCoordinateToCountryNameConverter.convert (inputCoordinate);
  }

  @Override
  public String getContinentNameAt (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return inputCoordinateToContinentNameConverter.convert (inputCoordinate);
  }
}
