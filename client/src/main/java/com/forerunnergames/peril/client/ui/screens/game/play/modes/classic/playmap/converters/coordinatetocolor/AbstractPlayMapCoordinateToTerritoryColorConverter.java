/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocolor;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.colors.TerritoryColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayMapCoordinateToTerritoryColorConverter <T extends TerritoryColor <?>>
        implements PlayMapCoordinateToTerritoryColorConverter <T>
{
  private final PlayMapCoordinateToRgbaColorConverter converter;
  private final Map <RgbaColor, T> rgbaToTerritoryColors = new HashMap<> ();

  protected AbstractPlayMapCoordinateToTerritoryColorConverter (final PlayMapCoordinateToRgbaColorConverter converter)
  {
    Arguments.checkIsNotNull (converter, "converter");

    this.converter = converter;
  }

  @Override
  public T convert (final Vector2 playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    final RgbaColor rgbaColor = converter.convert (playMapCoordinate);

    T territoryColor = rgbaToTerritoryColors.get (rgbaColor);

    if (territoryColor != null) return territoryColor;

    territoryColor = createTerritoryColor (rgbaColor);
    rgbaToTerritoryColors.put (rgbaColor, territoryColor);

    return territoryColor;
  }

  protected abstract T createTerritoryColor (final RgbaColor color);
}
