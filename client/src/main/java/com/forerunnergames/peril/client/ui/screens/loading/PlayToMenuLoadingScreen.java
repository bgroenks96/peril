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

package com.forerunnergames.peril.client.ui.screens.loading;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayToMenuLoadingScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (PlayToMenuLoadingScreen.class);
  private static final String LOADING_LABEL_TEXT = "LOADING";
  private static final float PROGRESS_BAR_ANIMATION_DURATION_SECONDS = 1.0f;
  private static final float PROGRESS_BAR_STEP_SIZE = 0.1f;
  private final ScreenChanger screenChanger;
  private final MouseInput mouseInput;
  private final AssetManager assetManager;
  private final MBassador <Event> eventBus;
  private final Cursor normalCursor;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final ProgressBar progressBar;
  private final Dialog quitDialog;
  private final Dialog errorDialog;
  private boolean isLoading = false;
  private float overallLoadingProgressPercent = 0.0f;
  private float currentLoadingProgressPercent = 0.0f;
  private float previousLoadingProgressPercent = 0.0f;

  public PlayToMenuLoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                                  final ScreenChanger screenChanger,
                                  final ScreenSize screenSize,
                                  final MouseInput mouseInput,
                                  final Batch batch,
                                  final AssetManager assetManager,
                                  final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.mouseInput = mouseInput;
    this.assetManager = assetManager;
    this.eventBus = eventBus;

    normalCursor = widgetFactory.createNormalCursor ();
    progressBar = widgetFactory.createProgressBar (PROGRESS_BAR_STEP_SIZE);
    progressBar.setAnimateDuration (PROGRESS_BAR_ANIMATION_DURATION_SECONDS);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createBackground ());

    final Table foregroundTable = new Table ().top ();
    foregroundTable.add ().height (870);
    foregroundTable.row ();
    foregroundTable.add (widgetFactory.createLabel (LOADING_LABEL_TEXT, Align.center, StyleSettings.LOADING_SCREEN_LOADING_TEXT_LABEL_STYLE)).size (700, 62);
    foregroundTable.row ().bottom ();
    foregroundTable.add (progressBar).size (700, 20).padBottom (128);

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport, batch);

    // @formatter:off
    quitDialog = widgetFactory.createQuitDialog ("Are you sure you want to quit Peril?", stage,
            new CancellableDialogListenerAdapter ()
            {
              @Override
              public void onSubmit ()
              {
                isLoading = false;
                eventBus.publishAsync (new QuitGameEvent ());
                resetLoadingProgress ();
                Gdx.app.exit ();
              }
            });
    // @formatter:on

    errorDialog = widgetFactory.createErrorDialog (stage, new DialogListenerAdapter ()
    {
      @Override
      public void onShow ()
      {
        isLoading = false;
        eventBus.publishAsync (new QuitGameEvent ());
      }

      @Override
      public void onSubmit ()
      {
        resetLoadingProgress ();
        screenChanger.toPreviousScreenOrSkipping (ScreenId.MAIN_MENU, ScreenId.PLAY_CLASSIC, ScreenId.PLAY_PERIL,
                                                  ScreenId.MENU_TO_PLAY_LOADING, ScreenId.PLAY_TO_MENU_LOADING,
                                                  ScreenId.SPLASH);
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
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());

    quitDialog.refreshAssets ();
    errorDialog.refreshAssets ();

    if (!loading ()) startLoading ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    quitDialog.update (delta);
    errorDialog.update (delta);

    stage.act (delta);
    stage.draw ();

    if (!loading ()) return;

    updateLoadingProgress ();

    if (loadingProgressIncreased ()) increaseLoadingProgressBy (getLoadingProgressIncrease ());
    if (isFinishedLoading ()) goToMenuScreen ();
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
    eventBus.unsubscribe (this);

    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();

    quitDialog.hide (null);
    errorDialog.hide (null);

    isLoading = false;
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
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
        // @formatter:off
        handleError (Strings
                .format ("There was a problem loading a game resource.\n\nResource Name: {}\nResource Type: {}\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                         event.getFileName (), event.getFileType ().getSimpleName (),
                         Throwables.getRootCause (event.getThrowable ()).getMessage (),
                         Strings.toString (event.getThrowable ())));
        // @formatter:on
      }
    });
  }

  private static void hideCursor ()
  {
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  private boolean loading ()
  {
    return isLoading;
  }

  private void startLoading ()
  {
    isLoading = true;
    currentLoadingProgressPercent = 0.0f;

    loadMenuAssetsAsync ();
  }

  private void goToMenuScreen ()
  {
    resetLoadingProgress ();
    unloadPlayScreenAssets ();

    screenChanger.toPreviousScreenOrSkipping (ScreenId.MAIN_MENU, ScreenId.PLAY_CLASSIC, ScreenId.PLAY_PERIL,
                                              ScreenId.MENU_TO_PLAY_LOADING, ScreenId.PLAY_TO_MENU_LOADING,
                                              ScreenId.SPLASH);
  }

  private void handleError (final String message)
  {
    log.error (message);

    errorDialog.setMessage (new DefaultMessage (message));
    errorDialog.show ();
  }

  private void unloadPlayScreenAssets ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.CLASSIC_MODE_PLAY_SCREEN_ASSET_DESCRIPTORS)
    {
      if (!assetManager.isLoaded (descriptor)) continue;
      assetManager.unload (descriptor);
    }
  }

  private void loadMenuAssetsAsync ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.MENU_SCREEN_ASSET_DESCRIPTORS)
    {
      assetManager.load (descriptor);
    }
  }

  private boolean isFinishedLoading ()
  {
    if (!isLoading)
      throw new IllegalStateException ("Cannot check whether finished loading because assets are not being loaded.");

    return progressBar.getVisualPercent () >= 1.0f && assetManager.getProgressLoading () >= 1.0f;
  }

  private void updateLoadingProgress ()
  {
    if (!isLoading)
      throw new IllegalStateException ("Cannot get loading progress percent because assets are not being loaded.");

    previousLoadingProgressPercent = currentLoadingProgressPercent;
    currentLoadingProgressPercent = assetManager.getProgressLoading ();
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

    log.debug ("Overall loading progress: {} (increased by {}).", overallLoadingProgressPercent, percent);
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
