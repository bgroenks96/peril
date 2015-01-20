package com.forerunnergames.peril.core.model.settings;

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

  private GameSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
