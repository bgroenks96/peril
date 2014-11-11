package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.tools.common.Arguments;

public final class PlayScreen extends InputAdapter implements Screen
{
  private final ScreenChanger screenChanger;
  private final Stage stage;

  public PlayScreen (final ScreenChanger screenChanger)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");

    this.screenChanger = screenChanger;

    // Layer 0 - background image
    final Stack rootStack = new Stack();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.playScreenBackground));

    // Layer 1 - map background image
    final Table tableL1 = new Table();
    tableL1.debugAll();
    rootStack.add (tableL1);
    tableL1.add().top().left();//.width (1704.0f).height (804.0f);//.padTop (14.0f).padLeft (14.0f);
    //tableL1.add (new Image (Assets.playScreenMapBackground)).expandY().fillY();
    //tableL1.add().expandX();

    stage = new Stage();
    stage.addActor (rootStack);
    stage.getViewport().setCamera (new OrthographicCamera (Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
  }

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        screenChanger.previous();
        return true;
      }
      case Input.Keys.RIGHT:
      {
        screenChanger.next();
        return true;
      }
      case Input.Keys.ESCAPE:
      {
        Gdx.app.exit();
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public void show()
  {
    Gdx.input.setInputProcessor (new InputMultiplexer (this, stage));
  }

  @Override
  public void hide()
  {
    Gdx.input.setInputProcessor (null);
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    stage.act (delta);
    stage.draw();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport().update (width, height, true);
  }

  @Override
  public void pause()
  {
  }

  @Override
  public void resume()
  {
  }

  @Override
  public void dispose()
  {
    stage.dispose();
  }
}
