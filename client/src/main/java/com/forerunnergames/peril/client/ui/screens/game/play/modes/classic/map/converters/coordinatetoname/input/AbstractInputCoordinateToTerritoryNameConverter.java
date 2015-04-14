package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractInputCoordinateToTerritoryNameConverter <T extends TerritoryName> implements
        InputCoordinateToTerritoryNameConverter <T>
{
  private final InputToScreenCoordinateConverter inputToScreenCoordinateConverter;
  private final ScreenCoordinateToTerritoryNameConverter <T> screenCoordinateToTerritoryNameConverter;

  protected AbstractInputCoordinateToTerritoryNameConverter (final InputToScreenCoordinateConverter inputToScreenCoordinateConverter,
                                                             final ScreenCoordinateToTerritoryNameConverter <T> screenCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (inputToScreenCoordinateConverter, "inputToScreenCoordinateConverter");
    Arguments.checkIsNotNull (screenCoordinateToTerritoryNameConverter, "screenCoordinateToTerritoryNameConverter");

    this.inputToScreenCoordinateConverter = inputToScreenCoordinateConverter;
    this.screenCoordinateToTerritoryNameConverter = screenCoordinateToTerritoryNameConverter;
  }

  @Override
  public T convert (final Vector2 inputCoordinate, final Vector2 screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return screenCoordinateToTerritoryNameConverter
            .convert (inputToScreenCoordinateConverter.convert (inputCoordinate), screenSize);
  }
}
