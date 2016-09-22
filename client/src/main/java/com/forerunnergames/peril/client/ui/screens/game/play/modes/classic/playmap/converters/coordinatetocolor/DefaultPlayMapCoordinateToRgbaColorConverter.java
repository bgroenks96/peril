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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocolor;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.color.RgbaColor;

public final class DefaultPlayMapCoordinateToRgbaColorConverter implements PlayMapCoordinateToRgbaColorConverter
{
  private final Pixmap rawPlayMapTerritoryColorsImage;
  private final IntMap <RgbaColor> rawToRgbaColors = new IntMap<> ();

  public DefaultPlayMapCoordinateToRgbaColorConverter (final Pixmap rawPlayMapTerritoryColorsImage)
  {
    Arguments.checkIsNotNull (rawPlayMapTerritoryColorsImage, "rawPlayMapTerritoryColorsImage");

    this.rawPlayMapTerritoryColorsImage = rawPlayMapTerritoryColorsImage;
  }

  @Override
  public RgbaColor convert (final Vector2 playMapCoordinate)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");

    final int rawColor = rawPlayMapTerritoryColorsImage.getPixel (Math.round (playMapCoordinate.x),
                                                                  Math.round (playMapCoordinate.y));

    RgbaColor rgbaColor = rawToRgbaColors.get (rawColor);

    if (rgbaColor != null) return rgbaColor;

    rgbaColor = new RgbaColor (rawColor);
    rawToRgbaColors.put (rawColor, rgbaColor);

    return rgbaColor;
  }
}
