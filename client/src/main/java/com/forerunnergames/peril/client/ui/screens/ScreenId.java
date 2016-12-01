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

package com.forerunnergames.peril.client.ui.screens;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public enum ScreenId
{
  NONE,
  SPLASH,
  MAIN_MENU,
  GAME_MODES_MENU,
  CLASSIC_GAME_MODE_MENU,
  PLAYER_PERIL_GAME_MODE_MENU,
  CLASSIC_GAME_MODE_CREATE_GAME_MENU,
  CLASSIC_GAME_MODE_JOIN_GAME_MENU,
  MENU_TO_PLAY_LOADING,
  PLAY_CLASSIC,
  PLAY_PERIL,
  PLAY_TO_MENU_LOADING;

  public static ScreenId fromGameMode (final GameMode mode)
  {
    Arguments.checkIsNotNull (mode, "mode");

    switch (mode)
    {
      case CLASSIC:
      {
        return PLAY_CLASSIC;
      }
      case PERIL:
      {
        return PLAY_PERIL;
      }
      default:
      {
        throw new UnsupportedOperationException (
                Strings.format ("Unsupported {}: [{}].", mode.getClass ().getSimpleName (), mode));
      }
    }
  }
}
