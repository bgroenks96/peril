package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultInputCoordinateToTerritoryNameConverter implements InputCoordinateToTerritoryNameConverter
{
  private final InputToScreenCoordinateConverter inputToScreenCoordinateConverter;
  private final ScreenCoordinateToTerritoryNameConverter screenCoordinateToTerritoryNameConverter;

  public DefaultInputCoordinateToTerritoryNameConverter (final InputToScreenCoordinateConverter inputToScreenCoordinateConverter,
                                                         final ScreenCoordinateToTerritoryNameConverter screenCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (inputToScreenCoordinateConverter, "inputToScreenCoordinateConverter");
    Arguments.checkIsNotNull (screenCoordinateToTerritoryNameConverter, "screenCoordinateToTerritoryNameConverter");

    this.inputToScreenCoordinateConverter = inputToScreenCoordinateConverter;
    this.screenCoordinateToTerritoryNameConverter = screenCoordinateToTerritoryNameConverter;
  }

  @Override
  public String convert (final Vector2 inputCoordinate)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");

    return screenCoordinateToTerritoryNameConverter
            .convert (inputToScreenCoordinateConverter.convert (inputCoordinate));
  }
}
