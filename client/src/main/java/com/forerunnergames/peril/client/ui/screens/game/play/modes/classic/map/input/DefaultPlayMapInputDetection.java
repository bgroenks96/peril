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
