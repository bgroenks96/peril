/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetoname.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
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
