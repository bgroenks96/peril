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

package com.forerunnergames.peril.client.application;

import com.amazonaws.util.IOUtils;

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
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads & writes user configurable settings to & from an external properties file.
 */
public final class ClientApplicationProperties
{
  public static final int CURRENT_VERSION = 1;
  public static final String PROPERTIES_FILE_SUBDIR = "peril" + File.separator + "settings";
  public static final String PROPERTIES_FILE_NAME = "settings.txt";
  public static final String VERSION_FILE_NAME = ".version";
  public static final String PROPERTIES_FILE_PATH = System.getProperty ("user.home") + File.separator
          + PROPERTIES_FILE_SUBDIR;
  public static final String VERSION_FILE_PATH = PROPERTIES_FILE_PATH;
  public static final String PROPERTIES_FILE_PATH_AND_NAME = PROPERTIES_FILE_PATH + File.separator
          + PROPERTIES_FILE_NAME;
  public static final String VERSION_FILE_PATH_AND_NAME = VERSION_FILE_PATH + File.separator + VERSION_FILE_NAME;
  public static final String WINDOW_WIDTH_PROPERTY_KEY = "window-width";
  public static final String WINDOW_HEIGHT_PROPERTY_KEY = "window-height";
  public static final String WINDOW_RESIZABLE_PROPERTY_KEY = "window-resizable";
  public static final String WINDOW_TITLE_PROPERTY_KEY = "window-title";
  public static final String FULLSCREEN_PROPERTY_KEY = "fullscreen";
  public static final String VSYNC_PROPERTY_KEY = "vsync";
  public static final String OPENGL_CORE_PROFILE_PROPERTY_KEY = "opengl-core-profile";
  public static final String MUSIC_ENABLED_PROPERTY_KEY = "music-enabled";
  public static final String MUSIC_VOLUME_PROPERTY_KEY = "music-volume";
  public static final String START_SCREEN_PROPERTY_KEY = "start-screen";
  public static final String UPDATE_ASSETS_KEY = "update-assets";
  public static final String UPDATED_ASSETS_LOCATION_KEY = "updated-assets-location";
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
          + "of your monitor(s)\n\n "
          + WINDOW_HEIGHT_PROPERTY_KEY
          + ": any whole number >= "
          + GraphicsSettings.MIN_INITIAL_WINDOW_HEIGHT
          + " and <= the max resolution height of your monitor(s)\n\n "
          + WINDOW_RESIZABLE_PROPERTY_KEY
          + ": true, false\n\n "
          + WINDOW_TITLE_PROPERTY_KEY
          + ": anything\n\n "
          + FULLSCREEN_PROPERTY_KEY
          + ": true, false\n\n "
          + VSYNC_PROPERTY_KEY
          + ": true, false\n\n "
          + OPENGL_CORE_PROFILE_PROPERTY_KEY
          + ": true, false\n\n "
          + MUSIC_ENABLED_PROPERTY_KEY
          + ": true, false\n\n "
          + MUSIC_VOLUME_PROPERTY_KEY
          + ": any decimal number >= " + MusicSettings.MIN_VOLUME + " and <= " + MusicSettings.MAX_VOLUME + "\n\n "
          + START_SCREEN_PROPERTY_KEY
          + ": " + Strings.toStringList (ScreenSettings.VALID_START_SCREENS, ", ", LetterCase.NONE, false) + "\n\n "
          + UPDATE_ASSETS_KEY
          + ": true, false ('true' will overwrite any asset customizations when running the game, "
          + "'false' will preserve any asset customizations, but your assets won't be updated - "
          + "which could crash the game - until you run the game with this set to 'true')\n\n "
          + UPDATED_ASSETS_LOCATION_KEY
          + ": Absolute location where updated assets can be found, either a local directory "
          + "(NOT surrounded by quotes, NO trailing slash) or an Amazon S3 bucket path ("
          + AssetSettings.VALID_S3_BUCKET_PATH_DESCRIPTION.replace ("\n", " ") + ")\n\n "
          + PLAYER_NAME_KEY
          + ": The name of your player, optional, can be left blank. "
          + GameSettings.VALID_PLAYER_NAME_DESCRIPTION.replace ("\n", " ") + "\n\n "
          + CLAN_NAME_KEY
          + ": Your clan tag, optional, can be left blank. "
          + GameSettings.VALID_CLAN_NAME_DESCRIPTION.replace ("\n", " ") + "\n\n "
          + SERVER_NAME_KEY
          + ": Your server title, optional, can be left blank. "
          + NetworkSettings.VALID_SERVER_NAME_DESCRIPTION.replace ("\n", " ") + "\n\n "
          + SERVER_ADDRESS_KEY
          + ": Your server address, optional, can be left blank. "
          + NetworkSettings.VALID_SERVER_ADDRESS_DESCRIPTION.replace ("\n", " ") + "\n\n "
          + CLASSIC_MODE_PLAYER_LIMIT_KEY
          + ": Number of players allowed in your classic game mode server, any whole number, "
          + ClassicGameRules.MIN_PLAYER_LIMIT + " to " + ClassicGameRules.MAX_PLAYER_LIMIT + "\n\n "
          + SPECTATOR_LIMIT_KEY
          + ": Number of spectators allowed in your server in any game mode, any whole number, "
          + GameSettings.MIN_SPECTATORS + " to " + GameSettings.MAX_SPECTATORS + "\n\n "
          + CLASSIC_MODE_MAP_NAME_KEY
          + ": Name of the map for your classic game mode server. "
          + GameSettings.VALID_MAP_NAME_DESCRIPTION.replace ("\n", " ") + "\n\n "
          + CLASSIC_MODE_WIN_PERCENT_KEY
          + ": % of the map that must be conquered to win the game in your classic game mode server, any whole "
          + "number, must be a multiple of 5, " + "5 (?) to " + ClassicGameRules.MAX_WIN_PERCENTAGE + ", the real "
          + "minimum valid win % cannot be determined until the map is loaded.\n\n "
          + CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY
          + ": Method of assigning initial countries to players in your classic game mode server: "
          + Strings.toStringList (", ", LetterCase.NONE, false, InitialCountryAssignment.values ()) + "\n\n "
          + "\n"
          + " If you've done your best to read & follow these instructions, and you're still having problems:\n\n"
          + " 1) Ask for help on our forums at http://community.forerunnergames.com\n"
          + " 2) Email us at support@forerunnergames.com. Please attach this file in the email, as well as any crash files.\n";
  // @formatter:on

  public static void set ()
  {
    final Properties properties = new Properties ();

    setDefaultValuesFor (properties);

    if (versionIsCurrent ())
    {
      if (loadPropertiesFromExistingFileInto (properties).failed ()) createNewPropertiesFileWith (properties);
    }
    else
    {
      updateVersion ();
      mergeOldPropertiesInto (properties);
      deleteOldPropertiesFile ();
      createNewPropertiesFileWith (properties);
    }

    configureApplicationSettingsFromProperties (properties);
  }

  private static boolean versionIsCurrent ()
  {
    final Optional <Integer> version = readVersion ();

    return version.isPresent () && version.get () == CURRENT_VERSION;
  }

  private static Optional <Integer> readVersion ()
  {
    try
    {
      String versionString = FileUtils.loadFile (VERSION_FILE_PATH_AND_NAME, StandardCharsets.UTF_8);
      versionString = versionString.trim ();

      return Optional.of (Integer.valueOf (versionString));
    }
    catch (final IOException | NumberFormatException e)
    {
      log.warn ("Failed to read settings version from \"{}\".\n\nDetails:\n\n{}", VERSION_FILE_PATH_AND_NAME,
                Throwables.getStackTraceAsString (e));

      return Optional.absent ();
    }
  }

  private static Properties intersection (final Properties propertiesUseValues,
                                          final Properties propertiesDontUseValues)
  {
    final Properties intersection = new Properties ();

    final Collection <Object> keys = new HashSet <> (propertiesUseValues.keySet ());
    keys.retainAll (propertiesDontUseValues.keySet ());

    for (final Object key : keys)
    {
      intersection.setProperty ((String) key, propertiesUseValues.getProperty ((String) key));
    }

    return intersection;
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

  private static void createNewPropertiesFileWith (final Properties properties)
  {
    log.info ("Attempting to create new settings file \"{}\"...", PROPERTIES_FILE_PATH_AND_NAME);

    createPropertiesFileDirectory ();

    try (final FileOutputStream out = new FileOutputStream (PROPERTIES_FILE_PATH_AND_NAME))
    {
      properties.store (out, PROPERTIES_FILE_COMMENTS);

      log.info ("Successfully created new settings file \"{}\".", PROPERTIES_FILE_PATH_AND_NAME);
    }
    catch (final IOException e)
    {
      log.error ("Failed to create \"{}\". Falling back to internal default values.\n\nDetails:\n\n{}",
                 PROPERTIES_FILE_PATH_AND_NAME, Throwables.getStackTraceAsString (e));
    }

    fixUrisInPropertiesFile ();
  }

  private static void createPropertiesFileDirectory ()
  {
    new File (PROPERTIES_FILE_PATH).mkdirs ();
  }

  private static void fixUrisInPropertiesFile ()
  {
    String propertiesFile;

    try (final FileInputStream in = new FileInputStream (PROPERTIES_FILE_PATH_AND_NAME))
    {
      propertiesFile = IOUtils.toString (in);

      // Replace escaped colon characters in property value URI's, e.g., http\://example.com => http://example.com
      propertiesFile = propertiesFile.replace ("\\://", "://");
    }
    catch (final IOException e)
    {
      log.error ("Failed to fix URI's in \"{}\".\n\nDetails:\n\n{}", PROPERTIES_FILE_PATH_AND_NAME,
                 Throwables.getStackTraceAsString (e));
      return;
    }

    // Re-write the properties file with the URI fix in place.
    try (final FileOutputStream out = new FileOutputStream (PROPERTIES_FILE_PATH_AND_NAME))
    {
      out.write (propertiesFile.getBytes (Charsets.UTF_8));
    }
    catch (final IOException e)
    {
      log.error ("Failed to fix URI's in \"{}\".\n\nDetails:\n\n{}", PROPERTIES_FILE_PATH_AND_NAME,
                 Throwables.getStackTraceAsString (e));
    }
  }

  private static void deleteOldPropertiesFile ()
  {
    try
    {
      log.info ("Attempting to delete your old settings file \"{}\"...", PROPERTIES_FILE_PATH_AND_NAME);

      Files.delete (Paths.get (PROPERTIES_FILE_PATH_AND_NAME));

      log.info ("Successfully deleted your old settings file \"{}\".", PROPERTIES_FILE_PATH_AND_NAME);
    }
    catch (final IOException e)
    {
      log.warn ("Failed to delete your old settings file \"{}\".\n\nDetails:\n\n{}", PROPERTIES_FILE_PATH_AND_NAME,
                Throwables.getStackTraceAsString (e));
    }
  }

  private static void updateVersion ()
  {
    try
    {
      log.info ("Attempting to create (or update) \"{}\"...", VERSION_FILE_PATH_AND_NAME);

      createVersionFileDirectory ();

      Files.write (Paths.get (VERSION_FILE_PATH_AND_NAME), ImmutableList.of (String.valueOf (CURRENT_VERSION)),
                   StandardCharsets.UTF_8);

      log.info ("Successfully created (or updated) \"{}\".", VERSION_FILE_PATH_AND_NAME);
    }
    catch (final IOException e)
    {
      log.error ("Failed to create (or update) \"{}\".\nIf \"{}\" exists, it will be upgraded "
              + "because we couldn't determine its version!\n\nDetails:\n\n{}", VERSION_FILE_PATH_AND_NAME,
                 PROPERTIES_FILE_PATH_AND_NAME, Throwables.getStackTraceAsString (e));
    }
  }

  private static void createVersionFileDirectory ()
  {
    new File (VERSION_FILE_PATH).mkdirs ();
  }

  private static void mergeOldPropertiesInto (final Properties properties)
  {
    properties.putAll (recoverOldPropertiesHavingKeysMatching (properties));
  }

  private static Result <String> loadPropertiesFromExistingFileInto (final Properties properties)
  {
    try
    {
      final String propertiesFileContents = FileUtils.loadFile (PROPERTIES_FILE_PATH_AND_NAME, StandardCharsets.UTF_8);

      // Deal with Windows path single backslashes being erased because it's a reserved character for continuing lines
      // by the Properties class.
      properties.load (new StringReader (propertiesFileContents.replace ("\\", "\\\\")));
    }
    catch (final IOException e)
    {
      final String failureReason = Throwables.getStackTraceAsString (e);
      log.warn ("Failed to load \"{}\".\n\nDetails:\n\n{}", PROPERTIES_FILE_PATH_AND_NAME, failureReason);
      return Result.failure (failureReason);
    }

    return Result.success ();
  }

  private static void setDefaultValuesFor (final Properties properties)
  {
    // @formatter:off
    properties.setProperty (WINDOW_WIDTH_PROPERTY_KEY, String.valueOf (GraphicsSettings.INITIAL_WINDOW_WIDTH));
    properties.setProperty (WINDOW_HEIGHT_PROPERTY_KEY, String.valueOf (GraphicsSettings.INITIAL_WINDOW_HEIGHT));
    properties.setProperty (WINDOW_RESIZABLE_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_WINDOW_RESIZABLE));
    properties.setProperty (WINDOW_TITLE_PROPERTY_KEY, String.valueOf (GraphicsSettings.WINDOW_TITLE));
    properties.setProperty (FULLSCREEN_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_FULLSCREEN));
    properties.setProperty (VSYNC_PROPERTY_KEY, String.valueOf (GraphicsSettings.IS_VSYNC_ENABLED));
    properties.setProperty (OPENGL_CORE_PROFILE_PROPERTY_KEY, String.valueOf (GraphicsSettings.USE_OPENGL_CORE_PROFILE));
    properties.setProperty (MUSIC_ENABLED_PROPERTY_KEY, String.valueOf (MusicSettings.IS_ENABLED));
    properties.setProperty (MUSIC_VOLUME_PROPERTY_KEY, String.valueOf (MusicSettings.INITIAL_VOLUME));
    properties.setProperty (START_SCREEN_PROPERTY_KEY, String.valueOf (ScreenSettings.START_SCREEN));
    properties.setProperty (UPDATE_ASSETS_KEY, String.valueOf (AssetSettings.UPDATE_ASSETS));
    properties.setProperty (UPDATED_ASSETS_LOCATION_KEY, AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION);
    properties.setProperty (PLAYER_NAME_KEY, InputSettings.INITIAL_PLAYER_NAME);
    properties.setProperty (CLAN_NAME_KEY, InputSettings.INITIAL_CLAN_NAME);
    properties.setProperty (SERVER_NAME_KEY, InputSettings.INITIAL_SERVER_NAME);
    properties.setProperty (SERVER_ADDRESS_KEY, InputSettings.INITIAL_SERVER_ADDRESS);
    properties.setProperty (CLASSIC_MODE_PLAYER_LIMIT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_PLAYER_LIMIT));
    properties.setProperty (SPECTATOR_LIMIT_KEY, String.valueOf (InputSettings.INITIAL_SPECTATOR_LIMIT));
    properties.setProperty (CLASSIC_MODE_MAP_NAME_KEY, Strings.toProperCase (String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_MAP_NAME)));
    properties.setProperty (CLASSIC_MODE_WIN_PERCENT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT));
    properties.setProperty (CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY, String.valueOf (InputSettings.INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT));
    // @formatter:on
  }

  private static Properties recoverOldPropertiesHavingKeysMatching (final Properties properties)
  {
    log.info ("Attempting to save your old settings...");

    final Properties allOldProperties = new Properties ();
    final Properties recoveredOldProperties = new Properties ();

    final Result <String> result = loadPropertiesFromExistingFileInto (allOldProperties);

    if (result.failed ())
    {
      log.error ("Failed to save your old settings.\n\nDetails:\n\n{}", result.getFailureReason ());
      return recoveredOldProperties;
    }

    recoveredOldProperties.putAll (intersection (allOldProperties, properties));

    log.info ("Successfully saved your old settings.");

    return recoveredOldProperties;
  }

  private static void configureApplicationSettingsFromProperties (final Properties properties)
  {
    // @formatter:off
    GraphicsSettings.INITIAL_WINDOW_WIDTH = parseInteger (WINDOW_WIDTH_PROPERTY_KEY, GraphicsSettings.MIN_INITIAL_WINDOW_WIDTH, properties);
    GraphicsSettings.INITIAL_WINDOW_HEIGHT = parseInteger (WINDOW_HEIGHT_PROPERTY_KEY, GraphicsSettings.MIN_INITIAL_WINDOW_HEIGHT, properties);
    GraphicsSettings.IS_WINDOW_RESIZABLE = parseBoolean (WINDOW_RESIZABLE_PROPERTY_KEY, properties);
    GraphicsSettings.WINDOW_TITLE = properties.getProperty (WINDOW_TITLE_PROPERTY_KEY);
    GraphicsSettings.IS_FULLSCREEN = parseBoolean (FULLSCREEN_PROPERTY_KEY, properties);
    GraphicsSettings.IS_VSYNC_ENABLED = parseBoolean (VSYNC_PROPERTY_KEY, properties);
    GraphicsSettings.USE_OPENGL_CORE_PROFILE = parseBoolean (OPENGL_CORE_PROFILE_PROPERTY_KEY, properties);
    MusicSettings.IS_ENABLED = parseBoolean (MUSIC_ENABLED_PROPERTY_KEY, properties);
    MusicSettings.INITIAL_VOLUME = parseFloat (MUSIC_VOLUME_PROPERTY_KEY, MusicSettings.MIN_VOLUME, MusicSettings.MAX_VOLUME, properties);
    ScreenSettings.START_SCREEN = parseStartScreen (START_SCREEN_PROPERTY_KEY, properties);
    AssetSettings.UPDATE_ASSETS = parseBoolean (UPDATE_ASSETS_KEY, properties);
    AssetSettings.ABSOLUTE_UPDATED_ASSETS_LOCATION = properties.getProperty (UPDATED_ASSETS_LOCATION_KEY);
    InputSettings.INITIAL_PLAYER_NAME = parsePlayerName (PLAYER_NAME_KEY, properties);
    InputSettings.INITIAL_CLAN_NAME = parseClanName (CLAN_NAME_KEY, properties);
    InputSettings.INITIAL_SERVER_NAME = parseServerName (SERVER_NAME_KEY, properties);
    InputSettings.INITIAL_SERVER_ADDRESS = parseServerAddress (SERVER_ADDRESS_KEY, properties);
    InputSettings.INITIAL_CLASSIC_MODE_PLAYER_LIMIT = parseInteger (CLASSIC_MODE_PLAYER_LIMIT_KEY, ClassicGameRules.MIN_PLAYER_LIMIT, ClassicGameRules.MAX_PLAYER_LIMIT, properties);
    InputSettings.INITIAL_SPECTATOR_LIMIT = parseInteger (SPECTATOR_LIMIT_KEY, GameSettings.MIN_SPECTATORS, GameSettings.MAX_SPECTATORS, properties);
    InputSettings.INITIAL_CLASSIC_MODE_MAP_NAME = parseClassicModeMapName (CLASSIC_MODE_MAP_NAME_KEY, properties);
    InputSettings.INITIAL_CLASSIC_MODE_WIN_PERCENT = parseInteger (CLASSIC_MODE_WIN_PERCENT_KEY, 5, ClassicGameRules.MAX_WIN_PERCENTAGE, 5, properties);
    InputSettings.INITIAL_CLASSIC_MODE_COUNTRY_ASSIGNMENT = parseEnum (CLASSIC_MODE_INITIAL_COUNTRY_ASSIGNMENT_KEY, InitialCountryAssignment.class, properties);
    // @formatter:on
  }

  private static Integer parseInteger (final String propertyKey, final int min, final Properties properties)
  {
    return parseInteger (propertyKey, min, Integer.MAX_VALUE, properties);
  }

  private static Integer parseInteger (final String propertyKey,
                                       final int min,
                                       final int max,
                                       final Properties properties)
  {
    return parseInteger (propertyKey, min, max, 1, properties);
  }

  private static Integer parseInteger (final String propertyKey,
                                       final int min,
                                       final int max,
                                       final int multiple,
                                       final Properties properties)
  {
    String propertyValue = null;

    try
    {
      propertyValue = properties.getProperty (propertyKey);

      final int value = Integer.valueOf (properties.getProperty (propertyKey));

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

  private static Float parseFloat (final String propertyKey,
                                   final float min,
                                   final float max,
                                   final Properties properties)
  {
    String propertyValue = null;

    try
    {
      propertyValue = properties.getProperty (propertyKey);

      final float value = Float.valueOf (properties.getProperty (propertyKey));

      if (value < min || value > max) throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));

      return value;
    }
    catch (final NumberFormatException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private static Boolean parseBoolean (final String propertyKey, final Properties properties)
  {
    final String propertyValue = properties.getProperty (propertyKey);

    if (!propertyValue.equalsIgnoreCase ("true") && !propertyValue.equalsIgnoreCase ("false"))
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));
    }

    return Boolean.valueOf (propertyValue);
  }

  private static <E extends Enum <E>> E parseEnum (final String propertyKey,
                                                   final Class <E> enumType,
                                                   final Properties properties)
  {
    String propertyValue = null;

    try
    {
      propertyValue = properties.getProperty (propertyKey);

      return Enum.valueOf (enumType, properties.getProperty (propertyKey).replace ("-", "_").toUpperCase ());
    }
    catch (final NullPointerException | IllegalArgumentException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private static ScreenId parseStartScreen (final String startScreenKey, final Properties properties)
  {
    final ScreenId startScreenId = parseEnum (startScreenKey, ScreenId.class, properties);

    if (!ScreenSettings.isValidStartScreen (startScreenId))
    {
      throw new RuntimeException (getParseErrorMessageFor (startScreenKey, properties.getProperty (startScreenKey)));
    }

    return startScreenId;
  }

  private static String parsePlayerName (final String playerNameKey, final Properties properties)
  {
    final String playerName = properties.getProperty (playerNameKey);

    if (!playerName.isEmpty () && !GameSettings.isValidPlayerNameWithoutClanTag (playerName))
    {
      throw new RuntimeException (getParseErrorMessageFor (playerNameKey, playerName));
    }

    return playerName;
  }

  private static String parseClanName (final String clanNameKey, final Properties properties)
  {
    final String clanName = properties.getProperty (clanNameKey);

    if (!clanName.isEmpty () && !GameSettings.isValidClanName (clanName))
    {
      throw new RuntimeException (getParseErrorMessageFor (clanNameKey, clanName));
    }

    return clanName;
  }

  private static String parseServerName (final String serverNameKey, final Properties properties)
  {
    final String serverName = properties.getProperty (serverNameKey);

    if (!serverName.isEmpty () && !NetworkSettings.isValidServerName (serverName))
    {
      throw new RuntimeException (getParseErrorMessageFor (serverNameKey, serverName));
    }

    return serverName;
  }

  private static String parseServerAddress (final String serverAddressKey, final Properties properties)
  {
    final String serverAddress = properties.getProperty (serverAddressKey);

    if (!serverAddress.isEmpty () && !NetworkSettings.isValidServerAddress (serverAddress))
    {
      throw new RuntimeException (getParseErrorMessageFor (serverAddressKey, serverAddress));
    }

    return serverAddress;
  }

  private static String parseClassicModeMapName (final String classicModeMapNameKey, final Properties properties)
  {
    final String mapName = properties.getProperty (classicModeMapNameKey);

    if (!GameSettings.isValidMapName (mapName))
    {
      throw new RuntimeException (getParseErrorMessageFor (classicModeMapNameKey, mapName));
    }

    return Strings.toProperCase (mapName);
  }

  private ClientApplicationProperties ()
  {
    Classes.instantiationNotAllowed ();
  }
}
