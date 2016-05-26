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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.application.ClientApplicationProperties;
import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.assets.AssetUpdater;
import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SplashScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (SplashScreen.class);
  private static final String LOADING_LABEL_TEXT = "LOADING";
  private static final float ONE_THIRD = 1.0f / 3.0f;
  private static final float TWO_THIRDS = 2.0f / 3.0f;
  private static final float PROGRESS_BAR_ANIMATION_DURATION_SECONDS = 1.0f;
  private static final float PROGRESS_BAR_STEP_SIZE = 0.1f;
  private static final float ASSET_UPDATING_PROGRESS_WEIGHT = TWO_THIRDS;
  private static final float UPDATED_ASSET_LOADING_PROGRESS_WEIGHT = ONE_THIRD;
  private final ScreenChanger screenChanger;
  private final MouseInput mouseInput;
  private final Cursor normalCursor;
  private final AssetUpdater assetUpdater;
  private final AssetManager assetManager;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final ProgressBar progressBar;
  private final Dialog quitDialog;
  private final Dialog errorDialog;
  private final int windowWidth;
  private final int windowHeight;
  private final Label loadingStatusTextLabel;
  private String statusMessageText = "";
  private boolean isLoading = false;
  private boolean isUpdatingAssets = false;
  private boolean isLoadingUpdatedAssets = false;
  private float currentLoadingProgressPercent = 0.0f;
  private float previousLoadingProgressPercent = 0.0f;
  private float overallLoadingProgressPercent = 0.0f;

  public SplashScreen (final SplashScreenWidgetFactory widgetFactory,
                       final ScreenChanger screenChanger,
                       final ScreenSize screenSize,
                       final MouseInput mouseInput,
                       final Batch batch,
                       final AssetUpdater assetUpdater,
                       final AssetManager assetManager,
                       final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.mouseInput = mouseInput;
    this.assetUpdater = assetUpdater;
    this.assetManager = assetManager;
    this.eventBus = eventBus;

    normalCursor = widgetFactory.createNormalCursor ();
    progressBar = widgetFactory.createProgressBar (PROGRESS_BAR_STEP_SIZE);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);

    final Image background = widgetFactory.createBackground ();
    windowWidth = Math.round (background.getWidth ());
    windowHeight = Math.round (background.getHeight ());

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (background);

    loadingStatusTextLabel = widgetFactory.createLabel ("", Align.left,
                                                        StyleSettings.LOADING_SCREEN_LOADING_STATUS_TEXT_LABEL_STYLE);

    // @formatter:off
    final Table foregroundTable = new Table ().top ();
    foregroundTable.add ().height (394);
    foregroundTable.row ();
    foregroundTable.add (widgetFactory.createLabel (LOADING_LABEL_TEXT, Align.center, StyleSettings.LOADING_SCREEN_LOADING_TEXT_LABEL_STYLE)).size (560, 62);
    foregroundTable.row ().bottom ();
    foregroundTable.add (progressBar).size (560, 20).padBottom (10);
    foregroundTable.row ();
    foregroundTable.add (loadingStatusTextLabel);
    // @formatter:on

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport, batch);

    // @formatter:off
    quitDialog = widgetFactory.createQuitDialog ("Are you sure you want to quit Peril?", stage, new DialogListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        Gdx.app.exit ();
      }
    });
    // @formatter:on

    errorDialog = widgetFactory.createErrorDialog (stage, new DialogListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        Gdx.app.exit ();
      }
    });

    stage.addActor (rootStack);

    stage.addListener (new ClickListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        stage.setKeyboardFocus (event.getTarget ());

        return false;
      }
    });

    stage.addCaptureListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        switch (keycode)
        {
          case Input.Keys.ESCAPE:
          {
            quitDialog.show ();

            return false;
          }
          default:
          {
            return false;
          }
        }
      }
    });

    final InputProcessor preInputProcessor = new InputAdapter ()
    {
      @Override
      public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
      {
        stage.setKeyboardFocus (null);

        return false;
      }
    };

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this);
  }

  @Override
  public void show ()
  {
    setSplashScreenDisplayMode ();

    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());

    quitDialog.refreshAssets ();
    errorDialog.refreshAssets ();

    startLoading ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    quitDialog.update (delta);
    errorDialog.update (delta);

    stage.act (delta);
    stage.draw ();

    if (!isLoading || errorDialog.isShown ()) return;

    updateLoadingProgress ();

    if (loadingProgressIncreased ()) increaseLoadingProgressBy (getLoadingProgressIncrease ());
    if (isFinishedUpdatingAssets () && !loadingUpdatedAssets ()) startLoadingUpdatedAssets ();
    if (isFinishedLoading ()) goToStartScreen ();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
    stage.getViewport ().setScreenPosition (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X,
                                            InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y);
  }

  @Override
  public void pause ()
  {
  }

  @Override
  public void resume ()
  {
  }

  @Override
  public void hide ()
  {
    resetLoadingProgress ();

    eventBus.unsubscribe (this);

    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();

    isLoading = false;
    isUpdatingAssets = false;
    isLoadingUpdatedAssets = false;

    for (final AssetDescriptor <?> descriptor : AssetSettings.UNLOAD_AFTER_SPLASH_SCREEN_ASSET_DESCRIPTORS)
    {
      assetManager.unload (descriptor);
    }

    quitDialog.hide (null);
    errorDialog.hide (null);

    setNextScreenDisplayMode ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
    shutDownAllLoading ();
  }

  @Handler
  void onEvent (final AssetLoadingErrorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        handleErrorDuringLoading (Strings.format ("A crash file has been created in \"{}\".\n\nThere was a problem "
                                                          + "loading a game resource.\n\nResource Name: {}\n"
                                                          + "Resource Type: {}\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                                                  CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                                                  event.getFileName (), event.getFileType ().getSimpleName (),
                                                  Throwables.getRootCause (event.getThrowable ()).getMessage (),
                                                  Throwables.getStackTraceAsString (event.getThrowable ())));
      }
    });
  }

  private static void hideCursor ()
  {
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  private void setSplashScreenDisplayMode ()
  {
    Gdx.graphics.setWindowedMode (windowWidth, windowHeight);
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
      log.warn ("Couldn't enable high DPI.\nCause:\n{}", Throwables.getStackTraceAsString (e));
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

  private boolean loadingUpdatedAssets ()
  {
    return isLoadingUpdatedAssets;
  }

  private void goToStartScreen ()
  {
    try
    {
      if (InputSettings.AUTO_JOIN_MULTIPLAYER_GAME && InputSettings.AUTO_CREATE_MULTIPLAYER_GAME)
      {
        throw new IllegalStateException (
                Strings.format ("Cannot auto-join & auto-create a multiplayer game simultaneously.\n\n"
                                        + "Please disable either '{}', '{}', or both in:\n\n{}.",
                                ClientApplicationProperties.AUTO_JOIN_MULTIPLAYER_GAME_KEY,
                                ClientApplicationProperties.AUTO_CREATE_MULTIPLAYER_GAME_KEY,
                                ClientApplicationProperties.PROPERTIES_FILE_PATH_AND_NAME));
      }
      else if (InputSettings.AUTO_JOIN_MULTIPLAYER_GAME)
      {
        log.info ("Auto-joining multiplayer game, skipping start screen.");

        screenChanger.toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_JOIN_GAME_MENU);
      }
      else if (InputSettings.AUTO_CREATE_MULTIPLAYER_GAME)
      {
        log.info ("Auto-creating multiplayer game, skipping start screen.");

        screenChanger.toScreen (ScreenId.MULTIPLAYER_CLASSIC_GAME_MODE_CREATE_GAME_MENU);
      }
      else
      {
        screenChanger.toScreen (ScreenSettings.START_SCREEN);
      }
    }
    catch (final GdxRuntimeException e)
    {
      handleErrorDuringLoading (Strings.format ("A crash file has been created in \"{}\".\n\nThe application "
                                                        + "encountered a problem.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                                                CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY, Throwables
                                                        .getRootCause (e).getMessage (), Throwables
                                                        .getStackTraceAsString (e)));
    }
  }

  private void startLoading ()
  {
    isLoading = true;
    startUpdatingAssets ();
  }

  private void handleErrorDuringLoading (final String message)
  {
    log.error (message);

    errorDialog.setMessage (new DefaultMessage (message));
    errorDialog.show ();

    shutDownAllLoading ();
  }

  private void shutDownAllLoading ()
  {
    assetUpdater.shutDown ();
    isLoading = false;
    isUpdatingAssets = false;
    isLoadingUpdatedAssets = false;
  }

  private void startUpdatingAssets ()
  {
    loadingStatusTextLabel.setText ("Please wait for asset updating to begin...");
    isUpdatingAssets = true;
    isLoadingUpdatedAssets = false;

    try
    {
      assetUpdater.updateAssets ();
    }
    catch (final RuntimeException e)
    {
      handleErrorDuringLoading (Strings
              .format ("A crash file has been created in \"{}\".\n\nThere was a problem "
                               + "updating a game resource.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                       CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                       Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e)));
    }
  }

  private void startLoadingUpdatedAssets ()
  {
    loadingStatusTextLabel.setText ("Please wait for asset loading to begin...");

    isUpdatingAssets = false;
    isLoadingUpdatedAssets = true;

    try
    {
      for (final AssetDescriptor <?> descriptor : AssetSettings.INITIAL_ASSET_DESCRIPTORS)
      {
        assetManager.load (descriptor);
      }
    }
    catch (final RuntimeException e)
    {
      handleErrorDuringLoading (Strings
              .format ("A crash file has been created in \"{}\".\n\nThere was a problem "
                               + "loading a game resource.\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                       CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                       Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e)));
    }
  }

  private boolean isFinishedUpdatingAssets ()
  {
    return assetUpdater.isFinished ();
  }

  private boolean isFinishedLoadingUpdatedAssets ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.INITIAL_ASSET_DESCRIPTORS)
    {
      if (!assetManager.isLoaded (descriptor.fileName)) return false;
    }

    return true;
  }

  private boolean isFinishedLoading ()
  {
    if (errorDialog.isShown ()) return false;

    if (!isLoading)
    {
      throw new IllegalStateException ("Cannot check whether loading is finished because it never began.");
    }

    return !isUpdatingAssets && progressBar.getVisualPercent () >= 1.0f && isFinishedLoadingUpdatedAssets ();
  }

  private void updateLoadingProgress ()
  {
    if (!isLoading)
    {
      throw new IllegalStateException ("Cannot update loading progress because loading never began.");
    }

    previousLoadingProgressPercent = currentLoadingProgressPercent;
    currentLoadingProgressPercent = assetUpdater.getProgressPercent ()
            * ASSET_UPDATING_PROGRESS_WEIGHT
            + (isLoadingUpdatedAssets ? assetManager.getProgressLoading () * UPDATED_ASSET_LOADING_PROGRESS_WEIGHT
                    : 0.0f);

    updateLoadingStatusText ();
  }

  private void updateLoadingStatusText ()
  {
    if (isUpdatingAssets && progressBar.getVisualPercent () < .001f)
    {
      statusMessageText = "Please wait for asset updating to begin...";
    }
    else if (isUpdatingAssets)
    {
      statusMessageText = "Updating assets... " + getPrettyLoadingPercentText ();
    }
    else if (isLoadingUpdatedAssets)
    {
      statusMessageText = "Loading assets... " + getPrettyLoadingPercentText ();
    }
    else
    {
      statusMessageText = "Finished!";
    }

    loadingStatusTextLabel.setText (statusMessageText);
  }

  private String getPrettyLoadingPercentText ()
  {
    return Math.round (progressBar.getVisualPercent () * 100.0f) + " %";
  }

  private boolean loadingProgressIncreased ()
  {
    return currentLoadingProgressPercent > previousLoadingProgressPercent;
  }

  private float getLoadingProgressIncrease ()
  {
    return currentLoadingProgressPercent - previousLoadingProgressPercent;
  }

  private void increaseLoadingProgressBy (final float percent)
  {
    overallLoadingProgressPercent += percent;

    progressBar.setValue (overallLoadingProgressPercent);

    log.trace ("Overall loading progress: {} (increased by {}).", overallLoadingProgressPercent, percent);
  }

  private void resetLoadingProgress ()
  {
    overallLoadingProgressPercent = 0.0f;
    progressBar.setAnimateDuration (0.0f);
    progressBar.setValue (0.0f);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);
  }

  private void showCursor ()
  {
    Gdx.graphics.setCursor (normalCursor);
  }
}
