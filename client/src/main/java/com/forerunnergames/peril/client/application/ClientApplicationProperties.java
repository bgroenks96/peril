package com.forerunnergames.peril.client.application;

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
  private static final Logger log = LoggerFactory.getLogger (ClientApplicationProperties.class);
  private static final String PROPERTIES_FILE_SUBDIR = "peril/settings";
  private static final String PROPERTIES_FILE_NAME = "settings.txt";
  private static final String WINDOW_WIDTH_PROPERTY_KEY = "window-width";
  private static final String WINDOW_HEIGHT_PROPERTY_KEY = "window-height";
  private static final String WINDOW_RESIZABLE_PROPERTY_KEY = "window-resizable";
  private static final String WINDOW_TITLE_PROPERTY_KEY = "window-title";
  private static final String FULLSCREEN_PROPERTY_KEY = "fullscreen";
  private static final String VSYNC_PROPERTY_KEY = "vsync";
  private static final String MUSIC_ENABLED_PROPERTY_KEY = "music-enabled";
  private static final String MUSIC_VOLUME_PROPERTY_KEY = "music-volume";
  private static final String START_SCREEN_PROPERTY_KEY = "start-screen";
  private static final String PROPERTIES_FILE_COMMENTS = "To reset this file, simply delete it, run peril-client, and it will be recreated with default values.\n"
          + "Valid values:\n"
          + WINDOW_WIDTH_PROPERTY_KEY
          + ": any whole number > 0\n"
          + WINDOW_HEIGHT_PROPERTY_KEY
          + ": any whole number > 0\n"
          + WINDOW_RESIZABLE_PROPERTY_KEY
          + ": true, false\n"
          + WINDOW_TITLE_PROPERTY_KEY
          + ": anything\n"
          + FULLSCREEN_PROPERTY_KEY
          + ": true, false\n"
          + VSYNC_PROPERTY_KEY
          + ": true, false\n"
          + MUSIC_ENABLED_PROPERTY_KEY
          + ": true, false\n"
          + MUSIC_VOLUME_PROPERTY_KEY
          + ": any decimal number 0.0 to 1.0 (inclusive)\n"
          + START_SCREEN_PROPERTY_KEY
          + ": "
          + Strings.toStringList (", ", LetterCase.NONE, false, ScreenId.values ());

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

    final Properties properties = new Properties (defaults);
    final String propertiesFilePath = System.getProperty ("user.home") + "/" + PROPERTIES_FILE_SUBDIR;
    final String propertiesFilePathAndName = propertiesFilePath + "/" + PROPERTIES_FILE_NAME;

    try
    {
      properties.load (new FileInputStream (propertiesFilePathAndName));
    }
    catch (final IOException e)
    {
      try
      {
        log.info ("Failed to load {}. Attempting to create it...", propertiesFilePathAndName);

        new File (propertiesFilePath).mkdirs ();

        defaults.store (new FileOutputStream (propertiesFilePathAndName), PROPERTIES_FILE_COMMENTS);

        log.info ("Successfully created {}.", propertiesFilePathAndName);
      }
      catch (final IOException e1)
      {
        log.warn ("Failed to load or create {}. Falling back to defaults.", propertiesFilePathAndName);
      }
    }

    GraphicsSettings.INITIAL_WINDOW_WIDTH = Integer.valueOf (properties.getProperty (WINDOW_WIDTH_PROPERTY_KEY));
    GraphicsSettings.INITIAL_WINDOW_HEIGHT = Integer.valueOf (properties.getProperty (WINDOW_HEIGHT_PROPERTY_KEY));
    GraphicsSettings.IS_WINDOW_RESIZABLE = Boolean.valueOf (properties.getProperty (WINDOW_RESIZABLE_PROPERTY_KEY));
    GraphicsSettings.WINDOW_TITLE = properties.getProperty (WINDOW_TITLE_PROPERTY_KEY);
    GraphicsSettings.IS_FULLSCREEN = Boolean.valueOf (properties.getProperty (FULLSCREEN_PROPERTY_KEY));
    GraphicsSettings.IS_VSYNC_ENABLED = Boolean.valueOf (properties.getProperty (VSYNC_PROPERTY_KEY));
    MusicSettings.IS_ENABLED = Boolean.valueOf (properties.getProperty (MUSIC_ENABLED_PROPERTY_KEY));
    MusicSettings.INITIAL_VOLUME = Float.valueOf (properties.getProperty (MUSIC_VOLUME_PROPERTY_KEY));
    ScreenSettings.START_SCREEN = ScreenId.valueOf (properties.getProperty (START_SCREEN_PROPERTY_KEY).toUpperCase ());
  }
}
