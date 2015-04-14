package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.tools.common.Arguments;

public final class MainMenuScreen extends InputAdapter implements Screen
{
  private final ScreenController screenController;
  private final ScreenMusic music;
  private final Stage stage;

  public MainMenuScreen (final ScreenController screenController, final ScreenMusic music)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (music, "music");

    this.screenController = screenController;
    this.music = music;

    // Layer 0 - menu background image
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.menuBackground));

    // Layer 1 - right menu background shadow
    final Table tableL1 = new Table ();
    rootStack.add (tableL1);
    tableL1.add ().width (666.0f);
    tableL1.add (new Image (Assets.rightMenuBackgroundShadow)).expandY ().fillY ();
    tableL1.add ().expandX ();

    // Layer 2 - text
    final Table tableL2 = new Table ();
    rootStack.add (tableL2);
    tableL2.add ().width (294.0f);
    tableL2.add (new Image (Assets.mainMenuText));
    tableL2.add ().expandX ();

    // Layer 3 - top & bottom menu bar extension shadows
    final Table tableL3 = new Table ();
    rootStack.add (tableL3);
    tableL3.add ().width (666.0f);
    tableL3.add (new Image (Assets.topMenuBarExtensionShadow)).width (288.0f).fillX ().top ();
    tableL3.add ().expandX ();
    tableL3.row ();
    tableL3.add ().expandY ();
    tableL3.row ();
    tableL3.add ().width (666.0f);
    tableL3.add (new Image (Assets.bottomMenuBarExtensionShadow)).width (288.0f).fillX ().bottom ();
    tableL3.add ().expandX ();

    // Layer 4 - left & right menu bar shadows
    final Table tableL4 = new Table ();
    rootStack.add (tableL4);
    tableL4.add ().width (294.0f);
    tableL4.add (new Image (Assets.leftMenuBarShadow)).expandY ().fillY ();
    tableL4.add ().width (330.0f);
    tableL4.add (new Image (Assets.rightMenuBarShadow)).expandY ().fillY ();
    tableL4.add ().expandX ();

    // Layer 5 - buttons
    final Table tableL5 = new Table ();
    rootStack.add (tableL5);
    tableL5.add ().width (294.0f);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING,
            GraphicsSettings.REFERENCE_SCREEN_WIDTH, GraphicsSettings.REFERENCE_SCREEN_HEIGHT, camera);
    stage = new Stage (viewport);
    stage.addActor (rootStack);
  }

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.RIGHT:
      {
        screenController.toScreen (ScreenId.PLAY_CLASSIC);
        return true;
      }
      case Input.Keys.ESCAPE:
      {
        Gdx.app.exit ();
        return true;
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

    Gdx.input.setInputProcessor (new InputMultiplexer (this, stage));

    if (MusicSettings.IS_ENABLED) music.start ();
  }

  @Override
  public void hide ()
  {
    Gdx.input.setInputProcessor (null);

    if (MusicSettings.IS_ENABLED) music.stop ();

    hideCursor ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
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
  public void dispose ()
  {
    stage.dispose ();
  }

  private void showCursor ()
  {
    Gdx.input.setCursorImage (Assets.menuNormalCursor, (int) InputSettings.MENU_NORMAL_MOUSE_CURSOR_HOTSPOT.x,
                              (int) InputSettings.MENU_NORMAL_MOUSE_CURSOR_HOTSPOT.y);
  }

  private void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }
}
