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
  public static final Pattern VALID_CLAN_NAME_TEXTFIELD_PATTERN = Pattern.compile ("[A-Za-z0-9]");
  public static String INITIAL_PLAYER_NAME = "";
  public static String INITIAL_CLAN_NAME = "";
  public static String INITIAL_SERVER_NAME = "";
  public static String INITIAL_SERVER_ADDRESS = "";
  public static int INITIAL_CLASSIC_MODE_PLAYER_LIMIT = ClassicGameRules.DEFAULT_PLAYER_LIMIT;
  public static int INITIAL_SPECTATOR_LIMIT = GameSettings.DEFAULT_SPECTATOR_LIMIT;
  public static String INITIAL_CLASSIC_MODE_MAP_NAME = GameSettings.DEFAULT_CLASSIC_MODE_MAP_NAME;
  public static int INITIAL_CLASSIC_MODE_WIN_PERCENT = ClassicGameRules.DEFAULT_WIN_PERCENTAGE;
  public static InitialCountryAssignment INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT = ClassicGameRules.DEFAULT_INITIAL_COUNTRY_ASSIGNMENT;

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
