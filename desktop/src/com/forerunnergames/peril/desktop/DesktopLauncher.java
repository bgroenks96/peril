package com.forerunnergames.peril.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.forerunnergames.peril.client.application.LibGdxGameFactory;
import com.forerunnergames.peril.client.settings.GraphicsSettings;

public final class DesktopLauncher
{
	public static void main (final String... args)
  {
    final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

    config.width = GraphicsSettings.REFERENCE_RESOLUTION_WIDTH;
    config.height = GraphicsSettings.REFERENCE_RESOLUTION_HEIGHT;
    config.fullscreen = GraphicsSettings.IS_FULLSCREEN;
    config.vSyncEnabled = GraphicsSettings.IS_VSYNC_ENABLED;
    config.resizable = GraphicsSettings.IS_WINDOW_RESIZABLE;
    config.title = GraphicsSettings.WINDOW_TITLE;

		new LwjglApplication (LibGdxGameFactory.create (), config);
	}
}
