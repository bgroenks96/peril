package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.input;

import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public abstract class AbstractInputCoordinateToTerritoryNameConverter <T extends TerritoryName> implements
        InputCoordinateToTerritoryNameConverter <T>
{
  private final InputToScreenCoordinateConverter inputToScreenCoordinateConverter;
  private final ScreenCoordinateToTerritoryNameConverter <T> screenCoordinateToTerritoryNameConverter;
  private Point2D screenCoordinate;

  protected AbstractInputCoordinateToTerritoryNameConverter (final InputToScreenCoordinateConverter inputToScreenCoordinateConverter,
                                                             final ScreenCoordinateToTerritoryNameConverter <T> screenCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (inputToScreenCoordinateConverter, "inputToScreenCoordinateConverter");
    Arguments.checkIsNotNull (screenCoordinateToTerritoryNameConverter, "screenCoordinateToTerritoryNameConverter");

    this.inputToScreenCoordinateConverter = inputToScreenCoordinateConverter;
    this.screenCoordinateToTerritoryNameConverter = screenCoordinateToTerritoryNameConverter;
  }

  @Override
  public T convert (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    screenCoordinate = inputToScreenCoordinateConverter.convert (inputCoordinate, screenSize);

    return screenCoordinateToTerritoryNameConverter.convert (screenCoordinate, screenSize);
  }
}
