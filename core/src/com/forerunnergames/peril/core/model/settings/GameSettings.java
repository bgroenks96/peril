package com.forerunnergames.peril.core.model.settings;

import com.forerunnergames.tools.common.Classes;

import java.util.regex.Pattern;

public final class GameSettings
{
  public static final int     MIN_PLAYERS                  = 2;
  public static final int     MAX_PLAYERS                  = 10;
  public static final int     MIN_SPECTATORS               = 0;
  public static final int     MAX_SPECTATORS               = 6;
  public static final int     MIN_PLAYER_NAME_LENGTH       = 1;
  public static final int     MAX_PLAYER_NAME_LENGTH       = 16;
  public static final int     MIN_PLAYER_CLAN_TAG_LENGTH   = 1;
  public static final int     MAX_PLAYER_CLAN_TAG_LENGTH   = 4;
  public static final int     MIN_SERVER_ADDRESS_LENGTH    = 4;
  public static final int     MAX_SERVER_ADDRESS_LENGTH    = 255;
  public static final int     MIN_SERVER_NAME_LENGTH       = 3;
  public static final int     MAX_SERVER_NAME_LENGTH       = 30;
  public static final int     MIN_SERVER_PORT_LENGTH       = 1;
  public static final int     MAX_SERVER_PORT_LENGTH       = 5;
  public static final Pattern PLAYER_NAME_PATTERN          = Pattern.compile ("[A-Za-z0-9]");
  public static final Pattern PLAYER_CLAN_TAG_PATTERN      = Pattern.compile ("[A-Za-z]");
  public static final Pattern SERVER_ADDRESS_PATTERN       = Pattern.compile ("[A-Za-z0-9.]");
  public static final Pattern SERVER_PORT_PATTERN          = Pattern.compile ("[0-9]{1,5}");
  public static final String  PLAYER_CLAN_TAG_START_SYMBOL = "[";
  public static final String  PLAYER_CLAN_TAG_END_SYMBOL   = "]";
  public static final Pattern SERVER_NAME_PATTERN          = Pattern.compile ("[A-Za-z0-9 ]");
  public static final Pattern COMMAND_PREFIX_PATTERN       = Pattern.compile ("^[\\\\/]");

  private GameSettings()
  {
    Classes.instantiationNotAllowed();
  }
}
