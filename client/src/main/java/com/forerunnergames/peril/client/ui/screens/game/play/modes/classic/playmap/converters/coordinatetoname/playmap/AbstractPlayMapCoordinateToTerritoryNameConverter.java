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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetoname.playmap;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.colors.TerritoryColor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.colortoname.TerritoryColorToNameConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.converters.coordinatetocolor.PlayMapCoordinateToTerritoryColorConverter;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractPlayMapCoordinateToTerritoryNameConverter <T extends TerritoryColor <?>> implements
        PlayMapCoordinateToTerritoryNameConverter
{
  private final TerritoryColorToNameConverter <T> territoryColorToNameConverter;
  private final PlayMapCoordinateToTerritoryColorConverter <T> playMapCoordinateToTerritoryColorConverter;

  protected AbstractPlayMapCoordinateToTerritoryNameConverter (final PlayMapCoordinateToTerritoryColorConverter <T> playMapCoordinateToTerritoryColorConverter,
                                                               final TerritoryColorToNameConverter <T> territoryColorToNameConverter)
  {
    Arguments.checkIsNotNull (playMapCoordinateToTerritoryColorConverter, "playMapCoordinateToTerritoryColorConverter");
    Arguments.checkIsNotNull (territoryColorToNameConverter, "territoryColorToNameConverter");

    this.playMapCoordinateToTerritoryColorConverter = playMapCoordinateToTerritoryColorConverter;
    this.territoryColorToNameConverter = territoryColorToNameConverter;
  }

  @Override
  public final String convert (final Vector2 playMapCoordinate)
  {
    final T color = playMapCoordinateToTerritoryColorConverter.convert (playMapCoordinate);

    return territoryColorToNameConverter.convert (color);
  }
}
