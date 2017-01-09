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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetoname.screen;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultScreenCoordinateToTerritoryNameConverter implements ScreenCoordinateToTerritoryNameConverter
{
  private final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter;
  private final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToTerritoryNameConverter;

  public DefaultScreenCoordinateToTerritoryNameConverter (final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                                                          final PlayMapCoordinateToTerritoryNameConverter playMapCoordinateToTerritoryNameConverter)
  {
    Arguments.checkIsNotNull (screenToPlayMapCoordinateConverter, "screenToPlayMapCoordinateConverter");
    Arguments.checkIsNotNull (playMapCoordinateToTerritoryNameConverter, "playMapCoordinateToTerritoryNameConverter");

    this.screenToPlayMapCoordinateConverter = screenToPlayMapCoordinateConverter;
    this.playMapCoordinateToTerritoryNameConverter = playMapCoordinateToTerritoryNameConverter;
  }

  @Override
  public String convert (final Vector2 screenCoordinate)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");

    return playMapCoordinateToTerritoryNameConverter
            .convert (screenToPlayMapCoordinateConverter.convert (screenCoordinate));
  }
}
