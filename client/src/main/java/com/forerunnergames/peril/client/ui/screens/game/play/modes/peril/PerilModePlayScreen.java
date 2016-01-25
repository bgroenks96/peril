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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilModePlayScreen extends InputAdapter implements Screen
{
  private final ScreenChanger screenChanger;
  private final Cursor normalCursor;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final InputProcessor inputProcessor;

  public PerilModePlayScreen (final PerilModePlayScreenWidgetFactory widgetFactory,
                              final ScreenChanger screenChanger,
                              final ScreenSize screenSize,
                              final MouseInput mouseInput,
                              final Batch batch,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.eventBus = eventBus;

    normalCursor = widgetFactory.createNormalCursor ();

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceWidth (), camera);

    stage = new Stage (viewport, batch)
    {
      @Override
      public boolean keyDown (final int keyCode)
      {
        return keyCode != Input.Keys.ESCAPE && super.keyDown (keyCode);
      }
    };

    final TankActor2 tankActor2 = widgetFactory.createTankActor2 ();

    stage.addActor (tankActor2);

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

    final InputProcessor preInputProcessor = new InputAdapter ()
    {
      @Override
      public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
      {
        stage.setKeyboardFocus (null);

        return false;
      }
    };

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this, tankActor2);
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.ESCAPE:
      {
        screenChanger.toScreen (ScreenId.PLAY_TO_MENU_LOADING);

        return false;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    stage.act (delta);
    stage.draw ();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
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

    Gdx.input.setInputProcessor (null);

    hideCursor ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);

    stage.dispose ();
  }

  private static void hideCursor ()
  {
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  private void showCursor ()
  {
    Gdx.graphics.setCursor (normalCursor);
  }  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    return false;
  }



  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    return false;
  }

}
