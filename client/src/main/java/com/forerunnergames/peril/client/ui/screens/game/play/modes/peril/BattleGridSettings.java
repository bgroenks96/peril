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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.forerunnergames.tools.common.Classes;

public final class BattleGridSettings
{
  public static final int BATTLE_GRID_ROW_COUNT = 15;
  public static final int BATTLE_GRID_COLUMN_COUNT = 15;
  public static final int BATTLE_GRID_CELL_WIDTH = 70;
  public static final int BATTLE_GRID_CELL_HEIGHT = 70;
  public static final int BATTLE_GRID_COLUMN_MIN_INDEX = 0;
  public static final int BATTLE_GRID_COLUMN_MAX_INDEX = BATTLE_GRID_COLUMN_COUNT - 1;
  public static final int BATTLE_GRID_ROW_MIN_INDEX = 0;
  public static final int BATTLE_GRID_ROW_MAX_INDEX = BATTLE_GRID_ROW_COUNT - 1;
  public static final boolean CONTINUOUS_UNIT_MOVEMENT = true;
  public static final boolean VISIBLE_GRID_LINES = false;

  private BattleGridSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
