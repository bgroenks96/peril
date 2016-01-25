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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.colortoname;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractTerritoryColorToNameConverter <T extends TerritoryColor <?>>
        implements TerritoryColorToNameConverter <T>
{
  private static final String UNKNOWN_TERRITORY_NAME = "";
  private final ImmutableMap <T, String> territoryColorsToNames;

  protected AbstractTerritoryColorToNameConverter (final ImmutableMap <T, String> territoryColorsToNames)
  {
    Arguments.checkIsNotNull (territoryColorsToNames, "territoryColorsToNames");

    this.territoryColorsToNames = territoryColorsToNames;
  }

  @Override
  public final String convert (final T territoryColor)
  {
    Arguments.checkIsNotNull (territoryColor, "territoryColor");

    if (!territoryColorsToNames.containsKey (territoryColor)) return UNKNOWN_TERRITORY_NAME;

    return territoryColorsToNames.get (territoryColor);
  }
}
