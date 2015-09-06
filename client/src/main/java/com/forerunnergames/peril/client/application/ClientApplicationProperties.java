package com.forerunnergames.peril.client.application;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads & writes user configurable settings to & from an external properties file.
 */
public final class ClientApplicationProperties
{
  public static final String PROPERTIES_FILE_SUBDIR = "peril" + File.separator + "settings";
  public static final String PROPERTIES_FILE_NAME = "settings.txt";
  public static final String PROPERTIES_FILE_PATH = System.getProperty ("user.home") + File.separator
          + PROPERTIES_FILE_SUBDIR;
  public static final String PROPERTIES_FILE_PATH_AND_NAME = PROPERTIES_FILE_PATH + File.separator
          + PROPERTIES_FILE_NAME;
  public static final String WINDOW_WIDTH_PROPERTY_KEY = "window-width";
  public static final String WINDOW_HEIGHT_PROPERTY_KEY = "window-height";
  public static final String WINDOW_RESIZABLE_PROPERTY_KEY = "window-resizable";
  public static final String WINDOW_TITLE_PROPERTY_KEY = "window-title";
  public static final String FULLSCREEN_PROPERTY_KEY = "fullscreen";
  public static final String VSYNC_PROPERTY_KEY = "vsync";
  public static final String MUSIC_ENABLED_PROPERTY_KEY = "music-enabled";
  public static final String MUSIC_VOLUME_PROPERTY_KEY = "music-volume";
  public static final String START_SCREEN_PROPERTY_KEY = "start-screen";
  public static final String UPDATE_ASSETS_KEY = "update-assets";
  public static final String UPDATED_ASSETS_DIRECTORY_KEY = "updated-assets-directory";
  public static final String PLAYER_NAME_KEY = "player-name";
  public static final String CLAN_NAME_KEY = "clan-tag";
  public static final String SERVER_NAME_KEY = "server-title";
  public static final String SERVER_ADDRESS_KEY = "server-address";
  public static final String CLASSIC_MODE_PLAYER_LIMIT_KEY = "players-classic-mode";
  public static final String SPECTATOR_LIMIT_KEY = "spectators";
  public static final String CLASSIC_MODE_MAP_NAME_KEY = "map-name-classic-mode";
  public static final String CLASSIC_MODE_WIN_PERCENT_KEY = "win-percent-classic-mode";
  public static final String CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY = "initial-country-assignment-classic-mode";
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (ClientApplicationProperties.class);
  private static final String PROPERTIES_FILE_COMMENTS = " Player-Configurable Peril Settings\n\n"
          + " IMPORTANT: All lines starting with a # don't do anything. Look near the bottom of the file for the actual settings!\n\n"
          + " To reset this file, simply delete it, restart Peril, and it will be recreated with default values.\n"
          + " You can delete any setting in here if you don't care about it, which will just cause it to use the default value.\n"
          + " To delete a setting, just delete the entire line containing the setting.\n\n" + " Valid values:\n\n "
          + WINDOW_WIDTH_PROPERTY_KEY
          + ": any whole number >= " + GraphicsSettings.MIN_INITIAL_WINDOW_WIDTH + " and <= the max resolution width "
          + "of your monitor(s)\n "
          + WINDOW_HEIGHT_PROPERTY_KEY
          + ": any whole number >= "
          + GraphicsSettings.MIN_INITIAL_WINDOW_HEIGHT
          + " and <= the max resolution height of your monitor(s)\n "
          + WINDOW_RESIZABLE_PROPERTY_KEY
          + ": true, false\n "
          + WINDOW_TITLE_PROPERTY_KEY
          + ": anything\n "
          + FULLSCREEN_PROPERTY_KEY
          + ": true, false\n "
          + VSYNC_PROPERTY_KEY
          + ": true, false\n "
          + MUSIC_ENABLED_PROPERTY_KEY
          + ": true, false\n "
          + MUSIC_VOLUME_PROPERTY_KEY
          + ": any decimal number >= " + MusicSettings.MIN_VOLUME + " and <= " + MusicSettings.MAX_VOLUME + "\n "
          + START_SCREEN_PROPERTY_KEY
          + ": " + Strings.toStringList (", ", LetterCase.NONE, false, ScreenId.values ()) + "\n "
          + UPDATE_ASSETS_KEY
          + ": true, false ('true' will overwrite any asset customizations when running the game, "
          + "'false' will preserve any asset customizations, but your assets won't be updated - "
          + "which could crash the game - until you run the game with this set to 'true')\n "
          + UPDATED_ASSETS_DIRECTORY_KEY
          + ": absolute directory (NOT surrounded by quotes, NO trailing slash) where updated assets can be found\n "
          + PLAYER_NAME_KEY
          + ": The name of your player, optional, can be left blank. "
          + GameSettings.VALID_PLAYER_NAME_DESCRIPTION.replace ("\n", " ") + "\n "
          + CLAN_NAME_KEY
          + ": Your clan tag, optional, can be left blank. "
          + GameSettings.VALID_CLAN_NAME_DESCRIPTION.replace ("\n", " ") + "\n "
          + SERVER_NAME_KEY
          + ": Your server title, optional, can be left blank. "
          + NetworkSettings.VALID_SERVER_NAME_DESCRIPTION.replace ("\n", " ") + "\n "
          + SERVER_ADDRESS_KEY
          + ": Your server address, optional, can be left blank. "
          + NetworkSettings.VALID_SERVER_ADDRESS_DESCRIPTION.replace ("\n", " ") + "\n "
          + CLASSIC_MODE_PLAYER_LIMIT_KEY
          + ": Number of players allowed in your classic game mode server, any whole number, "
          + ClassicGameRules.MIN_PLAYER_LIMIT + " to " + ClassicGameRules.MAX_PLAYER_LIMIT + "\n "
          + SPECTATOR_LIMIT_KEY
          + ": Number of spectators allowed in your server in any game mode, any whole number, "
          + GameSettings.MIN_SPECTATORS + " to " + GameSettings.MAX_SPECTATORS + "\n "
          + CLASSIC_MODE_MAP_NAME_KEY
          + ": Name of the map for your classic game mode server. "
          + GameSettings.VALID_MAP_NAME_DESCRIPTION.replace ("\n", " ") + "\n "
          + CLASSIC_MODE_WIN_PERCENT_KEY
          + ": % of the map that must be conquered to win the game in your classic game mode server, any whole "
          + "number, must be a multiple of 5, " + "5 (?) to " + ClassicGameRules.MAX_WIN_PERCENTAGE + ", the real "
          + "minimum valid win % cannot be determined until the map is loaded.\n "
          + CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY
          + ": Method of assigning initial countries to players in your classic game mode server: "
          + Strings.toStringList (", ", LetterCase.NONE, false, InitialCountryAssignment.values ()) + "\n "
          + "\n\n"
          + " If you've done your best to read & follow these instructions, and you're still having problems:\n\n"
          + " 1) Ask for help on our forums at http://community.forerunnergames.com\n"
          + " 2) Email us at support@forerunnergames.com. Please attach this file in the email, as well as any crash files.\n";
  // @formatter:on

  private final Properties properties;

  public ClientApplicationProperties ()
  {
    final Properties defaults = new Properties ();

    // @formatter:off
    defaults.setProperty (WINDOW_WIDTH_PROPERTY_KEY, String.valueOf (GraphicsSettings.INITIAL_WINDOW_WIDTH));
    defaults.setProperty (WINDOW_HEIGHT_PROPERTY_KEY, String.valueOf (GraphicsSettings.INITIAL_WINDOW_HEIGHT));
    defaults.setProperty (WINDOW_RESIZABLE_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_WINDOW_RESIZABLE));
    defaults.setProperty (WINDOW_TITLE_PROPERTY_KEY, String.valueOf (GraphicsSettings.WINDOW_TITLE));
    defaults.setProperty (FULLSCREEN_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_FULLSCREEN));
    defaults.setProperty (VSYNC_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_VSYNC_ENABLED));
    defaults.setProperty (MUSIC_ENABLED_PROPERTY_KEY, String.valueOf (MusicSettings.IS_ENABLED));
    defaults.setProperty (MUSIC_VOLUME_PROPERTY_KEY, String.valueOf (MusicSettings.INITIAL_VOLUME));
    defaults.setProperty (START_SCREEN_PROPERTY_KEY, String.valueOf (ScreenSettings.START_SCREEN));
    defaults.setProperty (UPDATE_ASSETS_KEY, String.valueOf (AssetSettings.UPDATE_ASSETS));
    defaults.setProperty (UPDATED_ASSETS_DIRECTORY_KEY, AssetSettings.ABSOLUTE_UPDATED_ASSETS_DIRECTORY);
    defaults.setProperty (PLAYER_NAME_KEY, InputSettings.INITIAL_PLAYER_NAME);
    defaults.setProperty (CLAN_NAME_KEY, InputSettings.INITIAL_CLAN_NAME);
    defaults.setProperty (SERVER_NAME_KEY, InputSettings.INITIAL_SERVER_NAME);
    defaults.setProperty (SERVER_ADDRESS_KEY, InputSettings.INITIAL_SERVER_ADDRESS);
    defaults.setProperty (CLASSIC_MODE_PLAYER_LIMIT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_PLAYER_LIMIT));
    defaults.setProperty (SPECTATOR_LIMIT_KEY, String.valueOf (InputSettings.INITIAL_SPECTATOR_LIMIT));
    defaults.setProperty (CLASSIC_MODE_MAP_NAME_KEY, Strings.toProperCase (String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_MAP_NAME)));
    defaults.setProperty (CLASSIC_MODE_WIN_PERCENT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT));
    defaults.setProperty (CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT));
    // @formatter:on

    properties = new Properties (defaults);

    try
    {
      final String propertiesFileContents = FileUtils.loadFile (PROPERTIES_FILE_PATH_AND_NAME, StandardCharsets.UTF_8);

      // Deal with Windows path single backslashes being erased because it's a reserved character for continuing lines
      // by the Properties class.
      properties.load (new StringReader (propertiesFileContents.replace ("\\", "\\\\")));
    }
    catch (final IOException e)
    {
      try
      {
        log.info ("Failed to load \"{}\". Attempting to create it...", PROPERTIES_FILE_PATH_AND_NAME);

        new File (PROPERTIES_FILE_PATH).mkdirs ();

        final FileOutputStream fileOutputStream = new FileOutputStream (PROPERTIES_FILE_PATH_AND_NAME);

        defaults.store (fileOutputStream, PROPERTIES_FILE_COMMENTS);

        fileOutputStream.close ();

        log.info ("Successfully created \"{}\".", PROPERTIES_FILE_PATH_AND_NAME);
      }
      catch (final IOException e1)
      {
        log.warn ("Failed to load or create \"{}\". Falling back to defaults.", PROPERTIES_FILE_PATH_AND_NAME);
      }
    }

    // @formatter:off
    GraphicsSettings.INITIAL_WINDOW_WIDTH = parseInteger (WINDOW_WIDTH_PROPERTY_KEY, GraphicsSettings.MIN_INITIAL_WINDOW_WIDTH);
    GraphicsSettings.INITIAL_WINDOW_HEIGHT = parseInteger (WINDOW_HEIGHT_PROPERTY_KEY, GraphicsSettings.MIN_INITIAL_WINDOW_HEIGHT);
    GraphicsSettings.IS_WINDOW_RESIZABLE = parseBoolean (WINDOW_RESIZABLE_PROPERTY_KEY);
    GraphicsSettings.WINDOW_TITLE = properties.getProperty (WINDOW_TITLE_PROPERTY_KEY);
    GraphicsSettings.IS_FULLSCREEN = parseBoolean (FULLSCREEN_PROPERTY_KEY);
    GraphicsSettings.IS_VSYNC_ENABLED = parseBoolean (VSYNC_PROPERTY_KEY);
    MusicSettings.IS_ENABLED = parseBoolean (MUSIC_ENABLED_PROPERTY_KEY);
    MusicSettings.INITIAL_VOLUME = parseFloat (MUSIC_VOLUME_PROPERTY_KEY, MusicSettings.MIN_VOLUME, MusicSettings.MAX_VOLUME);
    ScreenSettings.START_SCREEN = parseEnum (START_SCREEN_PROPERTY_KEY, ScreenId.class);
    AssetSettings.UPDATE_ASSETS = parseBoolean (UPDATE_ASSETS_KEY);
    AssetSettings.ABSOLUTE_UPDATED_ASSETS_DIRECTORY = properties.getProperty (UPDATED_ASSETS_DIRECTORY_KEY);
    InputSettings.INITIAL_PLAYER_NAME = parsePlayerName (PLAYER_NAME_KEY);
    InputSettings.INITIAL_CLAN_NAME = parseClanName (CLAN_NAME_KEY);
    InputSettings.INITIAL_SERVER_NAME = parseServerName (SERVER_NAME_KEY);
    InputSettings.INITIAL_SERVER_ADDRESS = parseServerAddress (SERVER_ADDRESS_KEY);
    InputSettings.INITIAL_CLASSIC_MODE_PLAYER_LIMIT = parseInteger (CLASSIC_MODE_PLAYER_LIMIT_KEY, ClassicGameRules.MIN_PLAYER_LIMIT, ClassicGameRules.MAX_PLAYER_LIMIT);
    InputSettings.INITIAL_SPECTATOR_LIMIT = parseInteger (SPECTATOR_LIMIT_KEY, GameSettings.MIN_SPECTATORS, GameSettings.MAX_SPECTATORS);
    InputSettings.INITIAL_CLASSIC_MODE_MAP_NAME = parseClassicModeMapName (CLASSIC_MODE_MAP_NAME_KEY);
    InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT = parseInteger (CLASSIC_MODE_WIN_PERCENT_KEY, 5, ClassicGameRules.MAX_WIN_PERCENTAGE, 5);
    InputSettings.INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT = parseEnum (CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY, InitialCountryAssignment.class);
    // @formatter:off
  }

  private static String getParseErrorMessageFor (final String propertyKey, final String propertyValue)
          throws RuntimeException
  {
    return "Oops! Looks like your " + PROPERTIES_FILE_NAME + " (located in: \"" + PROPERTIES_FILE_PATH
            + "\") has an invalid setting:\n\n" + propertyKey + "=" + propertyValue
            + " <-- HINT: The part after the = sign (" + propertyValue + ") ain't right!\n\n"
            + "Time to go back and actually read the instructions in your " + PROPERTIES_FILE_NAME
            + " and then try again... ;-)\n\nNerdy developer details:\n";
  }

  private Integer parseInteger (final String propertyKey, final int min)
  {
    return parseInteger (propertyKey, min, Integer.MAX_VALUE);
  }

  private Integer parseInteger (final String propertyKey, final int min, final int max)
  {
    return parseInteger (propertyKey, min, max, 1);
  }

  private Integer parseInteger (final String propertyKey, final int min, final int max, final int multiple)
  {
    String propertyValue = null;

    try
    {
      propertyValue = getPropertyValueFrom (propertyKey);

      final int value = Integer.valueOf (getPropertyValueFrom (propertyKey));

      if (value < min || value > max || value % multiple != 0)
      {
        throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));
      }

      return value;
    }
    catch (final NumberFormatException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private Float parseFloat (final String propertyKey, final float min, final float max)
  {
    String propertyValue = null;

    try
    {
      propertyValue = getPropertyValueFrom (propertyKey);

      final float value = Float.valueOf (getPropertyValueFrom (propertyKey));

      if (value < min || value > max) throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));

      return value;
    }
    catch (final NumberFormatException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private Boolean parseBoolean (final String propertyKey)
  {
    final String propertyValue = getPropertyValueFrom (propertyKey);

    if (!propertyValue.equalsIgnoreCase ("true") && !propertyValue.equalsIgnoreCase ("false"))
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));
    }

    return Boolean.valueOf (propertyValue);
  }

  private <E extends Enum <E>> E parseEnum (final String propertyKey, final Class <E> enumType)
  {
    String propertyValue = null;

    try
    {
      propertyValue = getPropertyValueFrom (propertyKey);

      return Enum.valueOf (enumType, getPropertyValueFrom (propertyKey).replace ("-", "_").toUpperCase ());
    }
    catch (final NullPointerException | IllegalArgumentException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private String parsePlayerName (final String playerNameKey)
  {
    final String playerName = getPropertyValueFrom (playerNameKey);

    if (!playerName.isEmpty () && !GameSettings.isValidPlayerNameWithoutClanTag (playerName))
    {
      throw new RuntimeException (getParseErrorMessageFor (playerNameKey, playerName));
    }

    return playerName;
  }

  private String parseClanName (final String clanNameKey)
  {
    final String clanName = getPropertyValueFrom (clanNameKey);

    if (!clanName.isEmpty () && !GameSettings.isValidClanName (clanName))
    {
      throw new RuntimeException (getParseErrorMessageFor (clanNameKey, clanName));
    }

    return clanName;
  }

  private String parseServerName (final String serverNameKey)
  {
    final String serverName = getPropertyValueFrom (serverNameKey);

    if (!serverName.isEmpty () && !NetworkSettings.isValidServerName (serverName))
    {
      throw new RuntimeException (getParseErrorMessageFor (serverNameKey, serverName));
    }

    return serverName;
  }

  private String parseServerAddress (final String serverAddressKey)
  {
    final String serverAddress = getPropertyValueFrom (serverAddressKey);

    if (!serverAddress.isEmpty () && !NetworkSettings.isValidServerAddress (serverAddress))
    {
      throw new RuntimeException (getParseErrorMessageFor (serverAddressKey, serverAddress));
    }

    return serverAddress;
  }

  private String parseClassicModeMapName (final String classicModeMapNameKey)
  {
    final String mapName = getPropertyValueFrom (classicModeMapNameKey);

    if (!GameSettings.isValidMapName (mapName))
    {
      throw new RuntimeException (getParseErrorMessageFor (classicModeMapNameKey, mapName));
    }

    return Strings.toProperCase (mapName);
  }

  private String getPropertyValueFrom (final String propertyKey)
  {
    return properties.getProperty (propertyKey);
  }
}
