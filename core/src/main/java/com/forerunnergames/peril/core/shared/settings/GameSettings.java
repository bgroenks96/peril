package com.forerunnergames.peril.core.shared.settings;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import java.util.regex.Pattern;

public final class GameSettings
{
  public static final int MIN_SPECTATORS = 0;
  public static final int MAX_SPECTATORS = 6;
  public static final int MIN_PLAYER_NAME_LENGTH = 1;
  public static final int MAX_PLAYER_NAME_LENGTH = 16;
  public static final int MIN_PLAYER_CLAN_TAG_LENGTH = 1;
  public static final int MAX_PLAYER_CLAN_TAG_LENGTH = 4;
  public static final Pattern PLAYER_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9]");
  public static final Pattern PLAYER_CLAN_TAG_PATTERN = Pattern.compile ("[A-Za-z]");
  public static final String PLAYER_CLAN_TAG_START_SYMBOL = "[";
  public static final String PLAYER_CLAN_TAG_END_SYMBOL = "]";
  public static final Pattern COMMAND_PREFIX_PATTERN = Pattern.compile ("^[\\\\/]");
  public static final String DEFAULT_CLASSIC_MODE_MAP_NAME = "classic";
  public static final int MIN_MAP_NAME_LENGTH = 1;
  public static final int MAX_MAP_NAME_LENGTH = 30;
  public static final Pattern VALID_MAP_NAME_PATTERN = Pattern.compile ("^(?=.{" + MIN_MAP_NAME_LENGTH + ","
          + MAX_MAP_NAME_LENGTH + "}$)(?!.* {2,})[a-zA-Z][a-zA-Z ]*[a-zA-Z]$");

  public static boolean isValidMapName (final String mapName)
  {
    Arguments.checkIsNotNull (mapName, "mapName");

    return VALID_MAP_NAME_PATTERN.matcher (mapName).matches ();
  }

  private GameSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
