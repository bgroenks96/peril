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

package com.forerunnergames.peril.client.ui.screens.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.assets.AssetUpdater;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.loading.AbstractLoadingScreen;
import com.forerunnergames.peril.client.ui.screens.loading.LoadingScreenStyle;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SplashScreen extends AbstractLoadingScreen
{
  private static final Logger log = LoggerFactory.getLogger (SplashScreen.class);
  private static final float ASSET_UPDATING_PROGRESS_WEIGHT = TWO_THIRDS;
  private static final float UPDATED_ASSET_LOADING_PROGRESS_WEIGHT = ONE_THIRD;
  private final AssetUpdater assetUpdater;
  private final int windowWidth;
  private final int windowHeight;
  private boolean isUpdatingAssets;
  private boolean isLoadingUpdatedAssets;
  private float previousAssetUpdatingProgressPercent;
  private float currentAssetUpdatingProgressPercent;

  public SplashScreen (final SplashScreenWidgetFactory widgetFactory,
                       final ScreenChanger screenChanger,
                       final ScreenSize screenSize,
                       final MouseInput mouseInput,
                       final Batch batch,
                       final AssetUpdater assetUpdater,
                       final AssetManager assetManager,
                       final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus, assetManager,
           LoadingScreenStyle.builder ().loadingTitleTextVerticalSpacer (394).loadingTitleTextLabelSize (560, 62)
                   .progressBarSize (560, 20).loadingStatusInitialText ("Please wait for asset updating to begin...")
                   .build ());

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetUpdater = assetUpdater;

    final Image background = widgetFactory.createBackground ();
    windowWidth = Math.round (background.getWidth ());
    windowHeight = Math.round (background.getHeight ());
  }

  @Override
  public void show ()
  {
    setSplashScreenDisplayMode ();
    super.show ();
    startUpdatingAssets ();
  }

  @Override
  public void hide ()
  {
    super.hide ();

    isUpdatingAssets = false;
    isLoadingUpdatedAssets = false;

    unloadAssetsSync (AssetSettings.UNLOAD_AFTER_SPLASH_SCREEN_ASSET_DESCRIPTORS);
    setNextScreenDisplayMode ();
  }

  @Override
  public void update (final float delta)
  {
    super.update (delta);

    if (!isUpdatingAssets) return;

    updateAssetUpdatingProgress ();
    if (assetUpdatingProgressIncreased ()) increaseProgressBy (getAssetUpdatingProgressPercentIncrease ());
    if (!isLoadingUpdatedAssets && isFinishedUpdatingAssets ()) startLoadingUpdatedAssets ();
  }

  @Override
  protected void onProgressFinished ()
  {
    goToStartScreen ();
  }

  @Override
  protected void onQuitDialogSubmit ()
  {
    Gdx.app.exit ();
  }

  @Override
  protected void onErrorDialogSubmit ()
  {
    Gdx.app.exit ();
  }

  @Override
  protected float normalize (final float assetLoadingProgressIncrease)
  {
    return assetLoadingProgressIncrease * UPDATED_ASSET_LOADING_PROGRESS_WEIGHT;
  }

  @Override
  public void dispose ()
  {
    super.dispose ();
    isUpdatingAssets = false;
    isLoadingUpdatedAssets = false;
    assetUpdater.shutDown ();
  }

  private void setSplashScreenDisplayMode ()
  {
    Gdx.graphics.setWindowedMode (windowWidth, windowHeight);
  }

  private void startUpdatingAssets ()
  {
    isUpdatingAssets = true;
    isLoadingUpdatedAssets = false;

    statusWithProgressPercent ("Updating assets...");

    try
    {
      assetUpdater.updateAssets ();
    }
    catch (final Exception e)
    {
      handleError ("A crash file has been created in \"{}\".\n\nThere was a problem updating a game resource.\n\n"
              + "Problem:\n\n{}\n\nDetails:\n\n{}", CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                   Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e));
    }
  }

  private void updateAssetUpdatingProgress ()
  {
    assert isUpdatingAssets;
    previousAssetUpdatingProgressPercent = currentAssetUpdatingProgressPercent;
    currentAssetUpdatingProgressPercent = assetUpdater.getProgressPercent () * ASSET_UPDATING_PROGRESS_WEIGHT;
  }

  private boolean assetUpdatingProgressIncreased ()
  {
    return currentAssetUpdatingProgressPercent > previousAssetUpdatingProgressPercent;
  }

  private float getAssetUpdatingProgressPercentIncrease ()
  {
    return currentAssetUpdatingProgressPercent - previousAssetUpdatingProgressPercent;
  }

  private boolean isFinishedUpdatingAssets ()
  {
    return assetUpdater.isFinished ();
  }

  private void startLoadingUpdatedAssets ()
  {
    isUpdatingAssets = false;
    isLoadingUpdatedAssets = true;
    statusWithProgressPercent ("Loading updated assets...");
    loadAssetsAsync (AssetSettings.INITIAL_ASSET_DESCRIPTORS);
  }

  private void goToStartScreen ()
  {
    try
    {
      if (InputSettings.AUTO_JOIN_MULTIPLAYER_GAME && InputSettings.AUTO_CREATE_MULTIPLAYER_GAME)
      {
        throw new IllegalStateException (Strings.format (
                                                         "Cannot auto-join & auto-create a multiplayer game simultaneously.\n\n"
                                                                 + "Please disable either '{}', '{}', or both in:\n\n{}.",
                                                         ClientApplicationProperties.AUTO_JOIN_MULTIPLAYER_GAME_KEY,
                                                         ClientApplicationProperties.AUTO_CREATE_MULTIPLAYER_GAME_KEY,
                                                         ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME));
      }
      else if (InputSettings.AUTO_JOIN_MULTIPLAYER_GAME)
      {
        log.info ("Auto-joining multiplayer game, skipping start screen.");

        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_JOIN_GAME_MENU);
      }
      else if (InputSettings.AUTO_CREATE_MULTIPLAYER_GAME)
      {
        log.info ("Auto-creating multiplayer game, skipping start screen.");

        toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_CREATE_GAME_MENU);
      }
      else
      {
        toScreen (ScreenSettings.START_SCREEN);
      }
    }
    catch (final GdxRuntimeException e)
    {
      handleError ("A crash file has been created in \"{}\".\n\nThe application encountered a problem.\n\n"
              + "Problem:\n\n{}\n\nDetails:\n\n{}", CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                   Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e));
    }
  }

  private void setNextScreenDisplayMode ()
  {
    configureWindowSettings ();
    configureDisplayMode ();
  }

  private void configureWindowSettings ()
  {
    configureHighDpi ();

    Gdx.graphics.setUndecorated (!GraphicsSettings.IS_WINDOW_DECORATED);
    Gdx.graphics.setResizable (GraphicsSettings.IS_WINDOW_RESIZABLE);
  }

  private void configureHighDpi ()
  {
    try
    {
      System.setProperty ("org.lwjgl.opengl.Display.enableHighDPI", String.valueOf (GraphicsSettings.USE_HIGH_DPI));
    }
    catch (final SecurityException e)
    {
      log.warn ("Couldn't enable high DPI.\nCause:\n\n", e);
    }
  }

  // TODO LWJGL 3: Add multi-monitor support.
  private void configureDisplayMode ()
  {
    final Graphics.DisplayMode currentMonitorMode = Gdx.graphics.getDisplayMode ();

    log.info ("Current monitor display mode: {}", currentMonitorMode);

    if (GraphicsSettings.IS_FULLSCREEN && Gdx.graphics.setFullscreenMode (currentMonitorMode))
    {
      log.info ("Successfully set fullscreen mode [{}].", Gdx.graphics.getDisplayMode ());
      return;
    }

    if (Gdx.graphics.setWindowedMode (GraphicsSettings.INITIAL_WINDOW_WIDTH, GraphicsSettings.INITIAL_WINDOW_HEIGHT))
    {
      log.info ("Successfully set windowed mode [{}].", Gdx.graphics.getDisplayMode ());
      return;
    }

    if (Gdx.graphics.setWindowedMode (currentMonitorMode.width, currentMonitorMode.height))
    {
      log.info ("Successfully set windowed mode [{}].", Gdx.graphics.getDisplayMode ());
      return;
    }

    throw new GdxRuntimeException (Strings.format ("Could not set any display mode."));
  }
}
