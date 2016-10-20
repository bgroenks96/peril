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

package com.forerunnergames.peril.common.settings;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

public final class GameSettings
{
  public static final int MIN_SPECTATORS = 0;
  public static final int MAX_SPECTATORS = 6;
  public static final int DEFAULT_SPECTATOR_LIMIT = MIN_SPECTATORS;
  public static final int MIN_PLAYER_NAME_LENGTH = 1;
  public static final int MAX_PLAYER_NAME_LENGTH = 16;
  public static final int MIN_CLAN_NAME_LENGTH = 1;
  public static final int MAX_CLAN_NAME_LENGTH = 4;
  public static final String CLAN_TAG_START_SYMBOL = "[";
  public static final String CLAN_TAG_END_SYMBOL = "]";
  public static final String PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL = " ";
  public static final Pattern COMMAND_PREFIX_PATTERN = Pattern.compile ("^[\\\\/]");
  public static final String DEFAULT_CLASSIC_MODE_MAP_NAME = "classic";
  public static final int MIN_MAP_NAME_LENGTH = 2;
  public static final int MAX_MAP_NAME_LENGTH = 30;
  public static final DieFaceValue DEFAULT_DIE_FACE_VALUE = DieFaceValue.SIX;
  public static final float BATTLE_INTERACTION_TIME_SECONDS = 1.5f;
  public static final float BATTLE_RESULT_VIEWING_TIME_SECONDS = 1.0f;
  public static final float BATTLE_RESPONSE_TIMEOUT_SECONDS = 5.0f;
  public static final float DICE_SPINNING_INTERVAL_SECONDS = 0.05f;
  public static final boolean CAN_ADD_REMOVE_DICE_IN_BATTLE = true;

  // @formatter:off

  public static final int MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH = CLAN_TAG_START_SYMBOL.length () + MIN_CLAN_NAME_LENGTH
          + CLAN_TAG_END_SYMBOL.length () + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL.length () + MIN_PLAYER_NAME_LENGTH;

  public static final int MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH = CLAN_TAG_START_SYMBOL.length () + MAX_CLAN_NAME_LENGTH
          + CLAN_TAG_END_SYMBOL.length () + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL.length () + MAX_PLAYER_NAME_LENGTH;

  public static final Pattern VALID_PLAYER_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9]{" + MIN_PLAYER_NAME_LENGTH + ","
          + MAX_PLAYER_NAME_LENGTH + "}");

  public static final Pattern VALID_CLAN_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9]{" + MIN_CLAN_NAME_LENGTH + ","
          + MAX_CLAN_NAME_LENGTH + "}");

  public static final Pattern VALID_CLAN_TAG_PATTERN = Pattern.compile (Pattern.quote (CLAN_TAG_START_SYMBOL) +
          VALID_CLAN_NAME_PATTERN.pattern () + Pattern.quote (CLAN_TAG_END_SYMBOL));

  public static final Pattern VALID_MAP_NAME_PATTERN = Pattern.compile ("^(?=.{" + MIN_MAP_NAME_LENGTH + ","
          + MAX_MAP_NAME_LENGTH + "}$)(?!.* {2,})[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$");

  public static final String VALID_PLAYER_NAME_DESCRIPTION =
            "1) " + MIN_PLAYER_NAME_LENGTH + " to " + MAX_PLAYER_NAME_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) Any combination of uppercase or lowercase is allowed.\n"
          + "3) No spaces.\n"
          + "4) No other type of whitespace.\n"
          + "5) No special characters.\n";

  public static final String VALID_CLAN_NAME_DESCRIPTION =
            "1) " + MIN_CLAN_NAME_LENGTH + " to " + MAX_CLAN_NAME_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) Any combination of uppercase or lowercase is allowed.\n"
          + "3) No spaces.\n"
          + "4) No other type of whitespace.\n"
          + "5) No special characters.\n";

  public static final String VALID_MAP_NAME_DESCRIPTION =
            "1) " + MIN_MAP_NAME_LENGTH + " to " + MAX_MAP_NAME_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) Any combination of uppercase or lowercase is allowed.\n"
          + "3) Single spaces are allowed, but cannot begin or end with a space.\n"
          + "4) No consecutive spaces.\n"
          + "5) No other type of whitespace.\n"
          + "6) No special characters.\n";

  // @formatter:on

  public static boolean isValidPlayerNameWithoutClanTag (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return VALID_PLAYER_NAME_PATTERN.matcher (playerName).matches ();
  }

  public static boolean isValidClanName (final String clanName)
  {
    Arguments.checkIsNotNull (clanName, "clanName");

    return VALID_CLAN_NAME_PATTERN.matcher (clanName).matches ();
  }

  public static boolean isValidClanTag (final String clanTag)
  {
    Arguments.checkIsNotNull (clanTag, "clanTag");

    return VALID_CLAN_TAG_PATTERN.matcher (clanTag).matches ();
  }

  // @formatter:off
  public static boolean isValidPlayerNameWithOptionalClanTag (final String playerNameWithOptionalClanTag)
  {
    Arguments.checkIsNotNull (playerNameWithOptionalClanTag, "playerNameWithOptionalClanTag");

    if (!playerNameWithOptionalClanTag.startsWith (CLAN_TAG_START_SYMBOL)) return isValidPlayerNameWithoutClanTag (playerNameWithOptionalClanTag);
    if (!playerNameWithOptionalClanTag.contains (CLAN_TAG_END_SYMBOL)) return false;
    if (playerNameWithOptionalClanTag.length () < MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;
    if (playerNameWithOptionalClanTag.length () > MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;

    final int clanNameStartIndexInclusive = playerNameWithOptionalClanTag.indexOf (CLAN_TAG_START_SYMBOL) + 1;
    final int clanNameEndIndexExclusive = playerNameWithOptionalClanTag.indexOf (CLAN_TAG_END_SYMBOL);

    if (clanNameStartIndexInclusive < 0 || clanNameEndIndexExclusive < 0) return false;
    if (clanNameStartIndexInclusive > clanNameEndIndexExclusive) return false;
    if (clanNameEndIndexExclusive > playerNameWithOptionalClanTag.length ()) return false;

    final String clanName = playerNameWithOptionalClanTag.substring (clanNameStartIndexInclusive, clanNameEndIndexExclusive);

    if (!isValidClanName (clanName)) return false;

    final String clanTag = getClanTag (clanName);
    final String playerNameWithoutClanTag = playerNameWithOptionalClanTag.replace (clanTag + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL, "");

    return isValidPlayerNameWithoutClanTag (playerNameWithoutClanTag);
  }
  // @formatter:on

  public static String getClanTag (final String clanName)
  {
    Arguments.checkIsNotNull (clanName, "clanName");

    if (!isValidClanName (clanName)) invalidClanName (clanName);

    return CLAN_TAG_START_SYMBOL + clanName + CLAN_TAG_END_SYMBOL;
  }

  public static String getPlayerNameWithOptionalClanTag (final String playerName, final String clanName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (clanName, "clanName");

    if (!isValidPlayerNameWithoutClanTag (playerName)) invalidPlayerName (playerName);
    if (clanName.isEmpty ()) return playerName;
    if (!isValidClanName (clanName)) invalidClanName (clanName);

    return getClanTag (clanName) + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL + playerName;
  }

  public static boolean isValidMapName (final String mapName)
  {
    Arguments.checkIsNotNull (mapName, "mapName");

    return VALID_MAP_NAME_PATTERN.matcher (mapName).matches ();
  }

  private static void invalidPlayerName (final String playerName)
  {
    throw new IllegalStateException (Strings.format ("Invalid player name [{}]. Valid player name rules:\n\n{}",
                                                     playerName, VALID_PLAYER_NAME_DESCRIPTION));
  }

  private static void invalidClanName (final String clanName)
  {
    throw new IllegalStateException (Strings.format ("Invalid clan name [{}]. Valid clan name rules:\n\n{}", clanName,
                                                     VALID_CLAN_NAME_PATTERN));
  }

  private GameSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
