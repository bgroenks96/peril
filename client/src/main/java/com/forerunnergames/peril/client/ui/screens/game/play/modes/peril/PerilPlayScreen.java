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

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class PerilPlayScreen extends InputAdapter implements Screen
{
  private final ScreenMusic music;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final InputProcessor inputProcessor;

  public PerilPlayScreen (final ScreenController screenController,
                          final TankActor2 tankActor2,
                          final ScreenMusic music,
                          final MBassador <Event> eventBus,
                          final Skin skin)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (tankActor2, "tankActor2");
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (skin, "skin");

    this.music = music;
    this.eventBus = eventBus;

    /*
    final Table rootTable = new Table ();
    rootTable.setFillParent (true);
    rootTable.add (tankActor2);
    rootTable.debugAll ();
    */

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING,
        GraphicsSettings.REFERENCE_SCREEN_WIDTH, GraphicsSettings.REFERENCE_SCREEN_HEIGHT, camera);

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
        Gdx.app.exit ();

        return false;
      }
      default:
      {
        return false;
      }
    }
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

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    if (MusicSettings.IS_ENABLED) music.start ();
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

    if (MusicSettings.IS_ENABLED) music.stop ();

    hideCursor ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);

    stage.dispose ();
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
