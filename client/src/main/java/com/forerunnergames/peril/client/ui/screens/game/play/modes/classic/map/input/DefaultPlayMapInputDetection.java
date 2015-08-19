package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultPlayMapInputDetection implements PlayMapInputDetection
{
  private final InputCoordinateToTerritoryNameConverter <CountryName> inputCoordinateToCountryNameConverter;
  private final InputCoordinateToTerritoryNameConverter <ContinentName> inputCoordinateToContinentNameConverter;

  public DefaultPlayMapInputDetection (final InputCoordinateToTerritoryNameConverter <CountryName> inputCoordinateToCountryNameConverter,
                                       final InputCoordinateToTerritoryNameConverter <ContinentName> inputCoordinateToContinentNameConverter)
  {
    Arguments.checkIsNotNull (inputCoordinateToCountryNameConverter, "inputCoordinateToCountryNameConverter");
    Arguments.checkIsNotNull (inputCoordinateToContinentNameConverter, "inputCoordinateToContinentNameConverter");

    this.inputCoordinateToCountryNameConverter = inputCoordinateToCountryNameConverter;
    this.inputCoordinateToContinentNameConverter = inputCoordinateToContinentNameConverter;
  }

  @Override
  public CountryName getCountryNameAt (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return inputCoordinateToCountryNameConverter.convert (inputCoordinate);
  }

  @Override
  public ContinentName getContinentNameAt (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return inputCoordinateToContinentNameConverter.convert (inputCoordinate);
  }
}
