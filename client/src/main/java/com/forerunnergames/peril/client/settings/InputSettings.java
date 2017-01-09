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

package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Classes;

import java.util.regex.Pattern;

public final class InputSettings
{
  public static final int ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X = 0;
  public static final int ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y = -3;
  public static final Vector2 NORMAL_MOUSE_CURSOR_HOTSPOT = new Vector2 (0, 0);
  public static final Pattern VALID_SERVER_NAME_TEXTFIELD_INPUT_PATTERN = Pattern.compile ("[A-Za-z0-9 ]");
  public static final Pattern VALID_PLAYER_NAME_TEXTFIELD_INPUT_PATTERN = Pattern.compile ("[A-Za-z0-9]");
  public static final Pattern VALID_CLAN_ACRONYM_TEXTFIELD_INPUT_PATTERN = Pattern.compile ("[A-Za-z0-9]");
  public static String INITIAL_PLAYER_NAME = "";
  public static String INITIAL_CLAN_ACRONYM = "";
  public static String INITIAL_SERVER_NAME = "";
  public static String INITIAL_SERVER_ADDRESS = "";
  public static int INITIAL_CLASSIC_MODE_HUMAN_PLAYER_LIMIT = ClassicGameRules.DEFAULT_HUMAN_PLAYER_LIMIT;
  public static int INITIAL_CLASSIC_MODE_AI_PLAYER_LIMIT = ClassicGameRules.DEFAULT_AI_PLAYER_LIMIT;
  public static int INITIAL_SPECTATOR_LIMIT = ClassicGameRules.DEFAULT_SPECTATOR_LIMIT;
  public static String INITIAL_CLASSIC_MODE_PLAY_MAP_NAME = GameSettings.DEFAULT_CLASSIC_MODE_PLAY_MAP_NAME;
  public static int INITIAL_CLASSIC_MODE_WIN_PERCENT = ClassicGameRules.DEFAULT_WIN_PERCENTAGE;
  public static InitialCountryAssignment INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT = ClassicGameRules.DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;
  public static boolean AUTO_JOIN_GAME = false;
  public static boolean AUTO_CREATE_GAME = false;

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
