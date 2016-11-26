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

package com.forerunnergames.peril.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.application.LibGdxGameFactory;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.peril.desktop.args.CommandLineArgs;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DesktopLauncher
{
  private static final Logger log = LoggerFactory.getLogger (DesktopLauncher.class);

  public static void main (final String... args)
  {
    Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler ()
    {
      @Override
      public void uncaughtException (final Thread t, final Throwable e)
      {
        final String errorMessage = Strings.format (
                                                    "The client application has crashed!\n\nA crash file has been "
                                                            + "created in \"{}\".\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                                                    CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                                                    Throwables.getRootCause (e).getMessage (), Strings.toString (e));

        log.error (errorMessage);

        if (Gdx.app != null) Gdx.app.exit ();

        showSwingBackupDialog (JOptionPane.ERROR_MESSAGE, 1000, 382, "Error", errorMessage);
      }
    });

    final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration ();

    ClientApplicationProperties.set ();

    try
    {
      System.setProperty ("org.lwjgl.opengl.Window.undecorated",
                          String.valueOf (!ScreenSettings.SPLASH_SCREEN_WINDOW_IS_DECORATED));
    }
    catch (final SecurityException e)
    {
      log.warn ("Couldn't make splash screen window undecorated.\nCause:\n\n", e);
    }

    config.width = ScreenSettings.SPLASH_SCREEN_WINDOW_WIDTH;
    config.height = ScreenSettings.SPLASH_SCREEN_WINDOW_HEIGHT;
    config.fullscreen = ScreenSettings.SPLASH_SCREEN_WINDOW_IS_FULLSCREEN;
    config.resizable = ScreenSettings.SPLASH_SCREEN_WINDOW_IS_RESIZABLE;
    config.vSyncEnabled = GraphicsSettings.IS_VSYNC_ENABLED;
    config.title = GraphicsSettings.WINDOW_TITLE;
    config.useGL30 = GraphicsSettings.USE_OPENGL_CORE_PROFILE;
    config.useHDPI = GraphicsSettings.USE_HIGH_DPI;
    config.allowSoftwareMode = true; // Try and avoid crashing on old, unsupported hardware.

    final CommandLineArgs jArgs = new CommandLineArgs ();
    final JCommander jCommander = new JCommander (jArgs);
    final StringBuilder usageStringBuilder = new StringBuilder ();

    try
    {
      jCommander.parse (args);
    }
    catch (final ParameterException e)
    {
      jCommander.usage (usageStringBuilder);
      log.info ("\n\n{}\n\nOptions with * are required\n\n{}", e.getMessage (), usageStringBuilder);
      System.exit (1);
    }

    if (jArgs.help)
    {
      jCommander.usage (usageStringBuilder);
      log.info ("\n\nOptions with * are required\n\n{}", usageStringBuilder);
      System.exit (0);
    }

    if (!jArgs.playerName.isEmpty ()) InputSettings.INITIAL_PLAYER_NAME = jArgs.playerName;
    if (!jArgs.clanAcronym.isEmpty ()) InputSettings.INITIAL_CLAN_ACRONYM = jArgs.clanAcronym;

    new LwjglApplication (LibGdxGameFactory.create (), config);

    // Show a warning if we're using software-rendering.
    if (((LwjglGraphics) Gdx.graphics).isSoftwareMode ())
    {
      final String warningMessage = "Peril is now running in software rendering mode.\n\n"
              + "It seems your video card does not support OpenGL hardware rendering. "
              + "This will not provide an optimal gaming experience; "
              + "performance and visuals may be severely degraded. "
              + "If this doesn't seem right, please try updating your video card drivers.";

      log.warn (warningMessage);

      showSwingBackupDialog (JOptionPane.WARNING_MESSAGE, 650, 244, "Warning", warningMessage);
    }
  }

  private static void showSwingBackupDialog (final int jOptionPaneMessageType,
                                             final int width,
                                             final int height,
                                             final String title,
                                             final String message)
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        final JTextArea textArea = new JTextArea (message);
        textArea.setLineWrap (true);
        textArea.setWrapStyleWord (true);
        textArea.setEditable (false);

        final JScrollPane scrollPane = new JScrollPane (textArea);
        scrollPane.setPreferredSize (new Dimension (width, height));

        final JOptionPane optionPane = new JOptionPane (scrollPane, jOptionPaneMessageType);
        optionPane.setPreferredSize (new Dimension (width, height));

        final JDialog dialog = optionPane.createDialog (title);
        dialog.setAlwaysOnTop (true);
        dialog.setVisible (true);
      }
    });
  }
}
