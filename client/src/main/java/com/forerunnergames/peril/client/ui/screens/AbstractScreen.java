/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.input.GdxKeyRepeatSystem;
import com.forerunnergames.peril.client.input.KeyRepeatListenerAdapter;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScreen extends AbstractScreenInputAdapter
{
  private final Logger log = LoggerFactory.getLogger (getClass ());
  private final WidgetFactory widgetFactory;
  private final ScreenChanger screenChanger;
  private final ScreenSize screenSize;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final Cursor normalCursor;
  private final Viewport viewport;
  private final Stage stage;
  private final InputMultiplexer inputProcessor;
  private final GdxKeyRepeatSystem keyRepeat;

  public AbstractScreen (final WidgetFactory widgetFactory,
                         final ScreenChanger screenChanger,
                         final ScreenSize screenSize,
                         final MouseInput mouseInput,
                         final Batch batch,
                         final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;
    this.screenChanger = screenChanger;
    this.screenSize = screenSize;
    this.mouseInput = mouseInput;
    this.eventBus = eventBus;

    normalCursor = widgetFactory.createNormalCursor ();

    viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (),
            new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ()));

    stage = new Stage (viewport, batch);
    stage.addListener (new AddKeyboardFocusListener (stage));
    stage.addCaptureListener (new EscapeKeyListener ());

    keyRepeat = new GdxKeyRepeatSystem (Gdx.input, new KeyRepeatListener ());
    keyRepeat.setKeyRepeatRate (Input.Keys.LEFT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.RIGHT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.UP, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.DOWN, 50);
    keyRepeat.setKeyRepeat (Input.Keys.LEFT, true);
    keyRepeat.setKeyRepeat (Input.Keys.RIGHT, true);
    keyRepeat.setKeyRepeat (Input.Keys.UP, true);
    keyRepeat.setKeyRepeat (Input.Keys.DOWN, true);
    keyRepeat.setKeyRepeat (Input.Keys.BACKSPACE, true);
    keyRepeat.setKeyRepeat (Input.Keys.FORWARD_DEL, true);

    // Should be last line in constructor to avoid any issues leaking 'this' pointer.
    inputProcessor = new InputMultiplexer (new RemoveKeyboardFocusInputProcessor (stage), stage, this);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show ()
  {
    Gdx.graphics.setCursor (normalCursor);
    stage.mouseMoved (mouseInput.x (), mouseInput.y ());
    Gdx.input.setInputProcessor (inputProcessor);
    eventBus.subscribe (this);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    keyRepeat.update ();
    update (delta);
    stage.draw ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
    stage.getViewport ().setScreenPosition (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X,
                                            InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void resume ()
  {
    stage.mouseMoved (mouseInput.x (), mouseInput.y ());
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void hide ()
  {
    eventBus.unsubscribe (this);
    stage.unfocusAll ();
    Gdx.input.setInputProcessor (null);
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
  }

  /**
   * Called once per frame in {@link #render(float)} just before {@link Stage#draw()}. Calls {@link Stage#act()}.
   *
   * @param delta
   *          The time in seconds since the last call to #render.
   */
  @OverridingMethodsMustInvokeSuper
  protected void update (final float delta)
  {
    stage.act (delta);
  }

  /**
   * Callback for when {@link Input.Keys#ESCAPE} is pressed.
   *
   * @return Whether or not the {@link Input.Keys#ESCAPE} key press is considered handled, or if it should be passed on
   *         to other {@link InputProcessor}'s for additional handling.
   */
  protected abstract boolean onEscape ();

  protected final MBassador <Event> getEventBus ()
  {
    return eventBus;
  }

  protected final Stage getStage ()
  {
    return stage;
  }

  protected final Viewport getViewport ()
  {
    return viewport;
  }

  protected final ScreenSize getScreenSize ()
  {
    return screenSize;
  }

  protected final MouseInput getMouseInput ()
  {
    return mouseInput;
  }

  protected final Vector2 getMousePosition ()
  {
    return mouseInput.position ();
  }

  protected final Vector2 getActualScreenSize ()
  {
    return screenSize.actual ();
  }

  protected final int getActualScreenWidth ()
  {
    return screenSize.actualWidth ();
  }

  protected final int getActualScreenHeight ()
  {
    return screenSize.actualHeight ();
  }

  protected final void addRootActor (final Actor actor)
  {
    Arguments.checkIsNotNull (actor, "actor");

    stage.addActor (actor);
  }

  protected final void addInputProcessor (final InputProcessor processor)
  {
    Arguments.checkIsNotNull (processor, "processor");

    inputProcessor.addProcessor (processor);
  }

  protected final void publish (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Publishing event [{}].", event);

    eventBus.publish (event);
  }

  protected final void publishAsync (final Event event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Publishing async event [{}].", event);

    eventBus.publishAsync (event);
  }

  protected final void subscribe (final Object subscriber)
  {
    Arguments.checkIsNotNull (subscriber, "subscriber");

    eventBus.subscribe (subscriber);
  }

  protected final void unsubscribe (final Object subscriber)
  {
    Arguments.checkIsNotNull (subscriber, "subscriber");

    eventBus.unsubscribe (subscriber);
  }

  protected final void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    screenChanger.toScreen (id);
  }

  protected final void toPreviousScreenOrSkipping (final ScreenId defaultScreenId, final ScreenId... skipScreenIds)
  {
    Arguments.checkIsNotNull (defaultScreenId, "defaultScreenId");
    Arguments.checkIsNotNull (skipScreenIds, "skipScreenIds");
    Arguments.checkHasNoNullElements (skipScreenIds, "skipScreenIds");

    screenChanger.toPreviousScreenOrSkipping (defaultScreenId, skipScreenIds);
  }

  protected final Dialog createQuitDialog (final String message, final CancellableDialogListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (listener, "listener");

    return widgetFactory.createQuitDialog (message, stage, listener);
  }

  protected final Dialog createConfirmationDialog (final String title,
                                                   final String message,
                                                   final CancellableDialogListener listener)
  {
    Arguments.checkIsNotNull (title, "title");
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (listener, "listener");

    return widgetFactory.createConfirmationDialog (title, message, stage, listener);
  }

  protected final Dialog createErrorDialog (final DialogListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    return widgetFactory.createErrorDialog (stage, listener);
  }

  protected final Dialog createErrorDialog ()
  {
    return widgetFactory.createErrorDialog (stage, new DialogListenerAdapter ());
  }

  private static final class AddKeyboardFocusListener extends ClickListener
  {
    private final Stage stage;

    AddKeyboardFocusListener (final Stage stage)
    {
      Arguments.checkIsNotNull (stage, "stage");

      this.stage = stage;
    }

    @Override
    public boolean touchDown (final InputEvent event, final float x, final float y, final int pointer, final int button)
    {
      stage.setKeyboardFocus (event.getTarget ());

      return false;
    }
  }

  private static final class RemoveKeyboardFocusInputProcessor extends InputAdapter
  {
    private final Stage stage;

    RemoveKeyboardFocusInputProcessor (final Stage stage)
    {
      Arguments.checkIsNotNull (stage, "stage");

      this.stage = stage;
    }

    @Override
    public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
    {
      stage.setKeyboardFocus (null);

      return false;
    }
  }

  private class EscapeKeyListener extends InputListener
  {
    @Override
    public boolean keyDown (final InputEvent event, final int keycode)
    {
      switch (keycode)
      {
        case Input.Keys.ESCAPE:
        {
          return screenChanger.isScreenTransitionInProgress () || onEscape ();
        }
        default:
        {
          return false;
        }
      }
    }
  }

  private final class KeyRepeatListener extends KeyRepeatListenerAdapter
  {
    @Override
    public void onKeyDownRepeating (final int keyCode)
    {
      AbstractScreen.this.onKeyDownRepeating (keyCode);
    }
  }
}
