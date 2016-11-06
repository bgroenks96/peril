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
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.playmap.PlayMapDirectoryType;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

public final class GameSettings
{
  public static final int MIN_SPECTATORS = 0;
  public static final int MAX_SPECTATORS = 6;
  public static final int DEFAULT_SPECTATOR_LIMIT = MIN_SPECTATORS;
  public static final int MIN_HUMAN_PLAYERS = 0;
  public static final int MIN_AI_PLAYERS = 0;
  public static final int DEFAULT_AI_PLAYER_LIMIT = MIN_AI_PLAYERS;
  public static final int MIN_PLAYER_NAME_LENGTH = 1;
  public static final int MAX_PLAYER_NAME_LENGTH = 16;
  public static final int MIN_CLAN_ACRONYM_LENGTH = 1;
  public static final int MAX_CLAN_ACRONYM_LENGTH = 4;
  public static final String CLAN_TAG_START_SYMBOL = "[";
  public static final String CLAN_TAG_END_SYMBOL = "]";
  public static final String PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL = " ";
  public static final String AI_PLAYER_CLAN_ACRONYM = "AI";
  public static final Pattern COMMAND_PREFIX_PATTERN = Pattern.compile ("^[\\\\/]");
  public static final String DEFAULT_CLASSIC_MODE_PLAY_MAP_NAME = "Classic";
  public static final String DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_NAME = "classic";
  public static final PlayMapDirectoryType DEFAULT_CLASSIC_MODE_PLAY_MAP_DIR_TYPE = PlayMapDirectoryType.INTERNAL;
  public static final PlayMapType DEFAULT_CLASSIC_MODE_PLAY_MAP_TYPE = PlayMapType.STOCK;
  public static final int MIN_PLAY_MAP_NAME_LENGTH = 2;
  public static final int MAX_PLAY_MAP_NAME_LENGTH = 30;
  public static final DieFaceValue DEFAULT_DIE_FACE_VALUE = DieFaceValue.SIX;
  public static final float BATTLE_INTERACTION_TIME_SECONDS = 1.5f;
  public static final float BATTLE_RESULT_VIEWING_TIME_SECONDS = 1.0f;
  public static final float BATTLE_RESPONSE_TIMEOUT_SECONDS = 5.0f;
  public static final float DICE_SPINNING_INTERVAL_SECONDS = 0.05f;
  public static final boolean CAN_ADD_REMOVE_DICE_IN_BATTLE = true;

  // @formatter:off

  public static final int MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH = CLAN_TAG_START_SYMBOL.length () + MIN_CLAN_ACRONYM_LENGTH
          + CLAN_TAG_END_SYMBOL.length () + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL.length () + MIN_PLAYER_NAME_LENGTH;

  public static final int MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH = CLAN_TAG_START_SYMBOL.length () + MAX_CLAN_ACRONYM_LENGTH
          + CLAN_TAG_END_SYMBOL.length () + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL.length () + MAX_PLAYER_NAME_LENGTH;

  public static final Pattern VALID_PLAYER_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9]{" + MIN_PLAYER_NAME_LENGTH + ","
          + MAX_PLAYER_NAME_LENGTH + "}");

  public static final Pattern VALID_CLAN_ACRONYM_PATTERN = Pattern.compile ("[A-Za-z0-9]{" + MIN_CLAN_ACRONYM_LENGTH
          + "," + MAX_CLAN_ACRONYM_LENGTH + "}");

  public static final Pattern VALID_CLAN_TAG_PATTERN = Pattern.compile (Pattern.quote (CLAN_TAG_START_SYMBOL) +
          VALID_CLAN_ACRONYM_PATTERN.pattern () + Pattern.quote (CLAN_TAG_END_SYMBOL));

  public static final Pattern VALID_PLAY_MAP_NAME_PATTERN = Pattern.compile ("^(?=.{" + MIN_PLAY_MAP_NAME_LENGTH + ","
          + MAX_PLAY_MAP_NAME_LENGTH + "}$)(?!.* {2,})[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$");

  public static final String VALID_PLAYER_NAME_DESCRIPTION =
            "1) " + MIN_PLAYER_NAME_LENGTH + " to " + MAX_PLAYER_NAME_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) Any combination of uppercase or lowercase is allowed.\n"
          + "3) No spaces.\n"
          + "4) No other type of whitespace.\n"
          + "5) No special characters.\n";

  public static final String VALID_CLAN_ACRONYM_DESCRIPTION =
            "1) " + MIN_CLAN_ACRONYM_LENGTH + " to " + MAX_CLAN_ACRONYM_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) AI (regardless of case) is reserved for AI players ;-)\n"
          + "3) Any combination of uppercase or lowercase is allowed.\n"
          + "4) No spaces.\n"
          + "5) No other type of whitespace.\n"
          + "6) No special characters.\n";

  public static final String VALID_PLAY_MAP_NAME_DESCRIPTION =
            "1) " + MIN_PLAY_MAP_NAME_LENGTH + " to " + MAX_PLAY_MAP_NAME_LENGTH + " alphanumeric characters are allowed.\n"
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

  public static boolean isValidHumanClanTag (final String clanTag)
  {
    Arguments.checkIsNotNull (clanTag, "clanTag");

    return VALID_CLAN_TAG_PATTERN.matcher (clanTag).matches ()
            && isValidHumanClanAcronym (getClanAcronymFromTag (clanTag));
  }

  public static boolean isValidHumanClanAcronym (final String clanAcronym)
  {
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    return !clanAcronym.equalsIgnoreCase (AI_PLAYER_CLAN_ACRONYM) && isValidClanAcronym (clanAcronym);
  }

  public static boolean isValidAiClanTag (final String clanTag)
  {
    Arguments.checkIsNotNull (clanTag, "clanTag");

    return VALID_CLAN_TAG_PATTERN.matcher (clanTag).matches ()
            && isValidAiClanAcronym (getClanAcronymFromTag (clanTag));
  }

  public static boolean isValidAiClanAcronym (final String clanAcronym)
  {
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    return clanAcronym.equals (AI_PLAYER_CLAN_ACRONYM) && isValidClanAcronym (clanAcronym);
  }

  public static String getClanAcronymFromTag (final String clanTag)
  {
    Arguments.checkIsNotNull (clanTag, "clanTag");

    if (!VALID_CLAN_TAG_PATTERN.matcher (clanTag).matches ()) return "";

    return clanTag.substring (clanTag.indexOf (CLAN_TAG_START_SYMBOL) + 1, clanTag.indexOf (CLAN_TAG_END_SYMBOL));
  }

  // @formatter:off

  public static boolean isValidHumanPlayerNameWithOptionalClanTag (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    if (!playerName.startsWith (CLAN_TAG_START_SYMBOL)) return isValidPlayerNameWithoutClanTag (playerName);
    if (!playerName.contains (CLAN_TAG_END_SYMBOL)) return false;
    if (playerName.length () < MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;
    if (playerName.length () > MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;

    final int clanAcronymStartIndexInclusive = playerName.indexOf (CLAN_TAG_START_SYMBOL) + 1;
    final int clanAcronymEndIndexExclusive = playerName.indexOf (CLAN_TAG_END_SYMBOL);

    if (clanAcronymStartIndexInclusive < 0 || clanAcronymEndIndexExclusive < 0) return false;
    if (clanAcronymStartIndexInclusive > clanAcronymEndIndexExclusive) return false;
    if (clanAcronymEndIndexExclusive > playerName.length ()) return false;

    final String clanAcronym = playerName.substring (clanAcronymStartIndexInclusive, clanAcronymEndIndexExclusive);

    if (!isValidHumanClanAcronym (clanAcronym)) return false;

    final String clanTag = getHumanClanTagFromAcryonym (clanAcronym);
    final String playerNameWithoutClanTag = playerName.replace (clanTag + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL, "");

    return isValidPlayerNameWithoutClanTag (playerNameWithoutClanTag);
  }

  public static boolean isValidAiPlayerNameWithMandatoryClanTag (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    if (!playerName.startsWith (CLAN_TAG_START_SYMBOL)) return false;
    if (!playerName.contains (CLAN_TAG_END_SYMBOL)) return false;
    if (playerName.length () < MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;
    if (playerName.length () > MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;

    final int clanAcronymStartIndexInclusive = playerName.indexOf (CLAN_TAG_START_SYMBOL) + 1;
    final int clanAcronymEndIndexExclusive = playerName.indexOf (CLAN_TAG_END_SYMBOL);

    if (clanAcronymStartIndexInclusive < 0 || clanAcronymEndIndexExclusive < 0) return false;
    if (clanAcronymStartIndexInclusive > clanAcronymEndIndexExclusive) return false;
    if (clanAcronymEndIndexExclusive > playerName.length ()) return false;

    final String clanAcronym = playerName.substring (clanAcronymStartIndexInclusive, clanAcronymEndIndexExclusive);

    if (!isValidAiClanAcronym (clanAcronym)) return false;

    final String clanTag = getAiClanTagFromAcryonym (clanAcronym);
    final String playerNameWithoutClanTag = playerName.replace (clanTag + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL, "");

    return isValidPlayerNameWithoutClanTag (playerNameWithoutClanTag);
  }
  
  // @formatter:on

  public static boolean hasClanTag (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    if (!playerName.startsWith (CLAN_TAG_START_SYMBOL)) return false;
    if (!playerName.contains (CLAN_TAG_END_SYMBOL)) return false;
    if (playerName.length () < MIN_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;
    if (playerName.length () > MAX_PLAYER_NAME_WITH_CLAN_TAG_LENGTH) return false;

    final int clanAcronymStartIndexInclusive = playerName.indexOf (CLAN_TAG_START_SYMBOL) + 1;
    final int clanAcronymEndIndexExclusive = playerName.indexOf (CLAN_TAG_END_SYMBOL);

    if (clanAcronymStartIndexInclusive < 0 || clanAcronymEndIndexExclusive < 0) return false;
    if (clanAcronymStartIndexInclusive > clanAcronymEndIndexExclusive) return false;
    if (clanAcronymEndIndexExclusive > playerName.length ()) return false;

    final String clanAcronym = playerName.substring (clanAcronymStartIndexInclusive, clanAcronymEndIndexExclusive);

    return isValidClanAcronym (clanAcronym);
  }

  public static boolean containsAiClanAcronym (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return getClanAcronymFromPlayerName (playerName).equalsIgnoreCase (AI_PLAYER_CLAN_ACRONYM);
  }

  public static String getClanAcronymFromPlayerName (final String playerName)
  {
    return getClanAcronymFromTag (getClanTagFromPlayerName (playerName));
  }

  public static String getClanTagFromPlayerName (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    if (!hasClanTag (playerName)) return "";

    return playerName.substring (playerName.indexOf (CLAN_TAG_START_SYMBOL),
                                 playerName.indexOf (CLAN_TAG_END_SYMBOL) + 1);
  }

  public static String getHumanClanTagFromAcryonym (final String clanAcronym)
  {
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    if (!isValidHumanClanAcronym (clanAcronym)) invalidClanAcronym (clanAcronym);

    return CLAN_TAG_START_SYMBOL + clanAcronym + CLAN_TAG_END_SYMBOL;
  }

  public static String getAiClanTagFromAcryonym (final String clanAcronym)
  {
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    if (!isValidAiClanAcronym (clanAcronym)) invalidClanAcronym (clanAcronym);

    return CLAN_TAG_START_SYMBOL + clanAcronym + CLAN_TAG_END_SYMBOL;
  }

  public static String getHumanPlayerNameWithOptionalClanTag (final String playerName, final String clanAcronym)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    if (!isValidPlayerNameWithoutClanTag (playerName)) invalidPlayerName (playerName);
    if (clanAcronym.isEmpty ()) return playerName;
    if (!isValidHumanClanAcronym (clanAcronym)) invalidClanAcronym (clanAcronym);

    return getHumanClanTagFromAcryonym (clanAcronym) + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL + playerName;
  }

  public static String getAiPlayerNameWithMandatoryClanTag (final String playerName)
  {
    return getAiPlayerNameWithMandatoryClanTag (playerName, AI_PLAYER_CLAN_ACRONYM);
  }

  public static String getAiPlayerNameWithMandatoryClanTag (final String playerName, final String clanAcronym)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (clanAcronym, "clanAcronym");

    if (!isValidPlayerNameWithoutClanTag (playerName)) invalidPlayerName (playerName);
    if (!isValidAiClanAcronym (clanAcronym)) invalidClanAcronym (clanAcronym);

    return getAiClanTagFromAcryonym (clanAcronym) + PLAYER_NAME_CLAN_TAG_SEPARATOR_SYMBOL + playerName;
  }

  public static boolean isValidPlayMapName (final String playMapName)
  {
    Arguments.checkIsNotNull (playMapName, "playMapName");

    return VALID_PLAY_MAP_NAME_PATTERN.matcher (playMapName).matches ();
  }

  // TODO Move to ClassicGameRules.
  public static int getAiPlayersLowerBoundClassicMode (final int humanPlayers)
  {
    Arguments.checkLowerInclusiveBound (humanPlayers, MIN_HUMAN_PLAYERS, "humanPlayers",
                                        "GameSettings.MIN_HUMAN_PLAYERS");
    Arguments.checkUpperInclusiveBound (humanPlayers, ClassicGameRules.MAX_PLAYERS, "humanPlayers",
                                        "GameSettings.MAX_HUMAN_PLAYERS");

    final int minAiPlayers = Math.max (ClassicGameRules.MIN_PLAYERS - humanPlayers, MIN_AI_PLAYERS);

    assert humanPlayers + minAiPlayers >= ClassicGameRules.MIN_PLAYERS;
    assert humanPlayers + minAiPlayers <= ClassicGameRules.MAX_PLAYERS;

    return minAiPlayers;
  }

  // TODO Move to ClassicGameRules.
  public static int getAiPlayersUpperBoundClassicMode (final int humanPlayers)
  {
    Arguments.checkLowerInclusiveBound (humanPlayers, MIN_HUMAN_PLAYERS, "humanPlayers",
                                        "GameSettings.MIN_HUMAN_PLAYERS");
    Arguments.checkUpperInclusiveBound (humanPlayers, ClassicGameRules.MAX_PLAYERS, "humanPlayers",
                                        "GameSettings.MAX_HUMAN_PLAYERS");

    final int aiPlayers = ClassicGameRules.MAX_PLAYERS - humanPlayers;

    assert humanPlayers + aiPlayers >= ClassicGameRules.MIN_PLAYERS;
    assert humanPlayers + aiPlayers <= ClassicGameRules.MAX_PLAYERS;

    return aiPlayers;
  }

  private static void invalidPlayerName (final String playerName)
  {
    throw new IllegalStateException (Strings.format ("Invalid player name [{}]. Valid player name rules:\n\n{}",
                                                     playerName, VALID_PLAYER_NAME_DESCRIPTION));
  }

  private static boolean isValidClanAcronym (final String clanAcronym)
  {
    return VALID_CLAN_ACRONYM_PATTERN.matcher (clanAcronym).matches ();
  }

  private static void invalidClanAcronym (final String clanAcronym)
  {
    throw new IllegalStateException (Strings.format ("Invalid clan acronym [{}]. Valid clan acronym rules:\n\n{}",
                                                     clanAcronym, VALID_CLAN_ACRONYM_PATTERN));
  }

  private GameSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
