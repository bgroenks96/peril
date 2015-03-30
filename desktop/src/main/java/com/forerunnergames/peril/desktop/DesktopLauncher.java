package com.forerunnergames.peril.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.application.LibGdxGameFactory;
import com.forerunnergames.peril.client.settings.GraphicsSettings;

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
      public void uncaughtException (final Thread thread, final Throwable throwable)
      {
        log.error ("The client application has crashed!\n", throwable);

        try
        {
          Gdx.app.exit ();
        }
        catch (final Throwable throwable2)
        {
          System.exit (1);
        }
      }
    });

    final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration ();

    new ClientApplicationProperties ();

    config.width = GraphicsSettings.INITIAL_WINDOW_WIDTH;
    config.height = GraphicsSettings.INITIAL_WINDOW_HEIGHT;
    config.fullscreen = GraphicsSettings.IS_FULLSCREEN;
    config.vSyncEnabled = GraphicsSettings.IS_VSYNC_ENABLED;
    config.resizable = GraphicsSettings.IS_WINDOW_RESIZABLE;
    config.title = GraphicsSettings.WINDOW_TITLE;

    new LwjglApplication (LibGdxGameFactory.create (), config);
  }
}
