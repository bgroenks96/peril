package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input.InputCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public final class PlayMapInputDetection
{
  private final InputCoordinateToTerritoryNameConverter <CountryName> inputCoordinateToCountryNameConverter;
  private final InputCoordinateToTerritoryNameConverter <ContinentName> inputCoordinateToContinentNameConverter;
  private CountryName countryName;
  private ContinentName continentName;

  public PlayMapInputDetection (final InputCoordinateToTerritoryNameConverter <CountryName> inputCoordinateToCountryNameConverter,
                                final InputCoordinateToTerritoryNameConverter <ContinentName> inputCoordinateToContinentNameConverter)
  {
    Arguments.checkIsNotNull (inputCoordinateToCountryNameConverter, "inputCoordinateToCountryNameConverter");
    Arguments.checkIsNotNull (inputCoordinateToContinentNameConverter, "inputCoordinateToContinentNameConverter");

    this.inputCoordinateToCountryNameConverter = inputCoordinateToCountryNameConverter;
    this.inputCoordinateToContinentNameConverter = inputCoordinateToContinentNameConverter;
  }

  public CountryName getCountryNameAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return inputCoordinateToCountryNameConverter.convert (inputCoordinate, screenSize);
  }

  public ContinentName getContinentNameAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return inputCoordinateToContinentNameConverter.convert (inputCoordinate, screenSize);
  }

  public String getPrintableTerritoryNamesAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    countryName = inputCoordinateToCountryNameConverter.convert (inputCoordinate, screenSize);
    continentName = inputCoordinateToContinentNameConverter.convert (inputCoordinate, screenSize);

    return Strings.toStringList (", ", LetterCase.PROPER, false, countryName.asString (), continentName.asString ());
  }
}
