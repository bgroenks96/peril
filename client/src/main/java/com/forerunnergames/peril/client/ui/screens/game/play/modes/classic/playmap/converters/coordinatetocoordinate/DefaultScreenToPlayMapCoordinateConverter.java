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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocoordinate;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenToPlayMapCoordinateConverter implements ScreenToPlayMapCoordinateConverter
{
  private final ScreenSize screenSize;
  private final Vector2 coordinate = new Vector2 ();
  private final Vector2 playMapReferenceSize;

  public DefaultScreenToPlayMapCoordinateConverter (final ScreenSize screenSize, final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    this.screenSize = screenSize;
    this.playMapReferenceSize = playMapReferenceSize;
  }

  @Override
  public Vector2 convert (final Vector2 actualScreenCoordinate)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");

    return coordinate.set (actualScreenCoordinate).scl (screenSize.actualToReferenceScaling ())
            .add (PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION)
            .scl (PlayMapSettings.actualToReferencePlayMapScaling (playMapReferenceSize));
  }
}
