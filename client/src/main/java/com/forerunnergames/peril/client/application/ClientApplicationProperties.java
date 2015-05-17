package com.forerunnergames.peril.client.application;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads & writes user configurable settings to & from an external properties file.
 */
public final class ClientApplicationProperties
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (ClientApplicationProperties.class);
  public static final String PROPERTIES_FILE_SUBDIR = "peril" + File.separator + "settings";
  public static final String PROPERTIES_FILE_NAME = "settings.txt";
  public static final String PROPERTIES_FILE_PATH = System.getProperty ("user.home") + File.separator + PROPERTIES_FILE_SUBDIR;
  public static final String PROPERTIES_FILE_PATH_AND_NAME = PROPERTIES_FILE_PATH + File.separator + PROPERTIES_FILE_NAME;
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
  private static final String PROPERTIES_FILE_COMMENTS = " Player-Configurable Peril Settings\n\n"
          + " IMPORTANT: All lines starting with a # don't do anything. Look near the bottom of the file for the actual settings!\n\n"
          + " To reset this file, simply delete it, restart Peril, and it will be recreated with default values.\n"
          + " You can delete any setting in here if you don't care about it, which will just cause it to use the default value.\n"
          + " To delete a setting, just delete the entire line containing the setting.\n\n" + " Valid values:\n\n "
          + WINDOW_WIDTH_PROPERTY_KEY
          + ": any whole number >= "
          + GraphicsSettings.MIN_INITIAL_WINDOW_WIDTH
          + " and <= the max resolution width of your monitor(s)\n "
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
          + ": any decimal number >= "
          + MusicSettings.MIN_VOLUME
          + " and <= "
          + MusicSettings.MAX_VOLUME
          + "\n "
          + START_SCREEN_PROPERTY_KEY
          + ": "
          + Strings.toStringList (", ", LetterCase.NONE, false, ScreenId.values ())
          + "\n "
          + UPDATE_ASSETS_KEY
          + ": true, false ('true' will overwrite any asset customizations when running the game, "
          + "'false' will preserve any asset customizations, but your assets won't be updated - "
          + "which could crash the game - until you run the game with this set to 'true')\n "
          + UPDATED_ASSETS_DIRECTORY_KEY
          + ": absolute directory (NOT surrounded by quotes, NO trailing slash) where updated assets can be found "
          + "\n\n"
          + " If you've done your best to read & follow these instructions, and you're still having problems:\n\n"
          + " 1) Ask for help on our forums at http://community.forerunnergames.com\n"
          + " 2) Email us at support@forerunnergames.com. Please attach this file in the email, as well as any crash files.\n";
  // @formatter:on

  private final Properties properties;

  public ClientApplicationProperties ()
  {
    final Properties defaults = new Properties ();

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

    properties = new Properties (defaults);

    try
    {
      properties.load (new FileInputStream (PROPERTIES_FILE_PATH_AND_NAME));
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
    // @formatter:on
  }

  private Integer parseInteger (final String propertyKey, final int min)
  {
    String propertyValue = null;

    try
    {
      propertyValue = getPropertyValueFrom (propertyKey);

      final int value = Integer.valueOf (getPropertyValueFrom (propertyKey));

      if (value < min) throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue));

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

      return Enum.valueOf (enumType, getPropertyValueFrom (propertyKey).toUpperCase ());
    }
    catch (final NullPointerException | IllegalArgumentException e)
    {
      throw new RuntimeException (getParseErrorMessageFor (propertyKey, propertyValue), e);
    }
  }

  private String getPropertyValueFrom (final String propertyKey)
  {
    return properties.getProperty (propertyKey);
  }

  private static String getParseErrorMessageFor (final String propertyKey, final String propertyValue) throws RuntimeException
  {
    return "Oops! Looks like your " + PROPERTIES_FILE_NAME + " (located in: \"" + PROPERTIES_FILE_PATH
            + "\") has an invalid setting:\n\n" + propertyKey + "=" + propertyValue
            + " <-- HINT: The part after the = sign (" + propertyValue + ") ain't right!\n\n"
            + "Time to go back and actually read the instructions in your " + PROPERTIES_FILE_NAME
            + " and then try again... ;-)\n\nNerdy developer details:\n";
  }
}
