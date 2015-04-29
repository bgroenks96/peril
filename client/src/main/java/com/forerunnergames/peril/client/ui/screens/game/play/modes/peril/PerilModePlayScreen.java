package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilModePlayScreen extends InputAdapter implements Screen
{
  private final ScreenChanger screenChanger;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final InputProcessor inputProcessor;

  public PerilModePlayScreen (final Skin skin,
                              final ScreenChanger screenChanger,
                              final TankActor2 tankActor2,
                              final ScreenSize screenSize,
                              final MouseInput mouseInput,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (tankActor2, "tankActor2");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenChanger = screenChanger;
    this.eventBus = eventBus;

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceWidth (), camera);

    stage = new Stage (viewport)
    {
      @Override
      public boolean keyDown (int keyCode)
      {
        if (keyCode == Input.Keys.ESCAPE) return false;

        return super.keyDown (keyCode);
      }
    };

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
      public boolean touchDown (int screenX, int screenY, int pointer, int button)
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
        screenChanger.toPreviousScreenOr (ScreenId.MAIN_MENU);

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

  @Override
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

  private void showCursor ()
  {
    Gdx.input.setCursorImage (Assets.playScreenNormalCursor,
                              (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.x,
                              (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.y);
  }

  private void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }
}
