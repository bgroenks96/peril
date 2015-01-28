package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

public final class PlayScreen extends InputAdapter implements Screen
{
  private final ScreenController screenController;
  private final PlayMapActor playMapActor;
  private final TerritoryTextActor territoryTextActor;
  private final ScreenMusic music;
  private final Stage stage;
  private Size2D currentScreenSize;

  // @formatter:off
  public PlayScreen (final ScreenController screenController,
                     final PlayMapActor playMapActor,
                     final TerritoryTextActor territoryTextActor,
                     final ScreenMusic music)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryNameTextActor");
    Arguments.checkIsNotNull (music, "music");

    this.screenController = screenController;
    this.playMapActor = playMapActor;
    this.territoryTextActor = territoryTextActor;
    this.music = music;

    // Layer 0 - background image
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.playScreenBackground));

    // Layer 1 - map background image
    final Table tableL1 = new Table ();
    tableL1.top ().left ();
    tableL1.add (new Image (Assets.playScreenMapBackground))
                    .padTop ((float) PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_X)
                    .padLeft ((float) PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_Y);
    tableL1.setTouchable (Touchable.disabled);
    rootStack.add (tableL1);

    // Layer 2 - map foreground image
    final Table tableL2 = new Table ();
    tableL2.top ().left ();
    tableL2.add (new Image (Assets.playScreenMapForeground))
                    .padTop ((float) PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_X)
                    .padLeft ((float) PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION_Y);
    tableL2.setTouchable (Touchable.disabled);
    rootStack.add (tableL2);

    rootStack.addActor (playMapActor);

    territoryTextActor.toFront();
    rootStack.addActor (territoryTextActor);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport =
                    new ScalingViewport (
                                    GraphicsSettings.VIEWPORT_SCALING,
                                    GraphicsSettings.REFERENCE_SCREEN_WIDTH,
                                    GraphicsSettings.REFERENCE_SCREEN_HEIGHT, camera);

    stage = new Stage (viewport);
    stage.addActor (rootStack);
  }
  // @formatter:on

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        screenController.toPreviousScreenOr (ScreenId.MAIN_MENU);

        return true;
      }
      case Input.Keys.ESCAPE:
      {
        Gdx.app.exit ();

        return true;
      }
      case Input.Keys.NUM_1:
      {
        playMapActor.clearCountryColors ();

        return true;
      }
      case Input.Keys.NUM_2:
      {
        playMapActor.randomizeCountryColors ();

        return true;
      }
      case Input.Keys.NUM_3:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (2);

        return true;
      }
      case Input.Keys.NUM_4:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (3);

        return true;
      }
      case Input.Keys.NUM_5:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (4);

        return true;
      }
      case Input.Keys.NUM_6:
      {
        playMapActor.setClassicCountryColors ();

        return true;
      }
      case Input.Keys.NUM_7:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.ORANGE, PlayerColor.RED);

        return true;
      }
      case Input.Keys.NUM_8:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.BLUE, PlayerColor.CYAN);

        return true;
      }
      case Input.Keys.NUM_9:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.PINK, PlayerColor.PURPLE);

        return true;
      }
      case Input.Keys.NUM_0:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.ORANGE, PlayerColor.RED, PlayerColor.BROWN);

        return true;
      }
      case Input.Keys.Q:
      {
        playMapActor.setCountriesTo (PlayerColor.BLUE);

        return true;
      }
      case Input.Keys.W:
      {
        playMapActor.setCountriesTo (PlayerColor.BROWN);

        return true;
      }
      case Input.Keys.E:
      {
        playMapActor.setCountriesTo (PlayerColor.CYAN);

        return true;
      }
      case Input.Keys.R:
      {
        playMapActor.setCountriesTo (PlayerColor.GOLD);

        return true;
      }
      case Input.Keys.T:
      {
        playMapActor.setCountriesTo (PlayerColor.GREEN);

        return true;
      }
      case Input.Keys.Y:
      {
        playMapActor.setCountriesTo (PlayerColor.ORANGE);

        return true;
      }
      case Input.Keys.U:
      {
        playMapActor.setCountriesTo (PlayerColor.PINK);

        return true;
      }
      case Input.Keys.I:
      {
        playMapActor.setCountriesTo (PlayerColor.PURPLE);

        return true;
      }
      case Input.Keys.O:
      {
        playMapActor.setCountriesTo (PlayerColor.RED);

        return true;
      }
      case Input.Keys.P:
      {
        playMapActor.setCountriesTo (PlayerColor.SILVER);

        return true;
      }
      case Input.Keys.F:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Assets.playScreenMapForeground.setFilter (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.G:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
        Assets.playScreenMapForeground.setFilter (Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.H:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        Assets.playScreenMapForeground.setFilter (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        return true;
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
    final boolean isHandled = playMapActor.touchDown (new Point2D (screenX, screenY), button, getScreenSize ());

    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return isHandled;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return playMapActor.touchUp (new Point2D (screenX, screenY), button, getScreenSize ());
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    final boolean isHandled = playMapActor.mouseMoved (new Point2D (screenX, screenY), getScreenSize ());

    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return isHandled;
  }

  @Override
  public void show ()
  {
    Gdx.input.setInputProcessor (new InputMultiplexer (this, stage));

    if (MusicSettings.IS_ENABLED) music.start ();
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
  public void hide ()
  {
    Gdx.input.setInputProcessor (null);

    if (MusicSettings.IS_ENABLED) music.stop ();
  }

  @Override
  public void dispose ()
  {
    stage.dispose ();
  }

  private Size2D getScreenSize ()
  {
    if (currentScreenSize != null && currentScreenSize.getWidth () == Gdx.graphics.getWidth ()
                    && currentScreenSize.getHeight () == Gdx.graphics.getHeight ())
    {
      return currentScreenSize;
    }

    currentScreenSize = new Size2D (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());

    return currentScreenSize;
  }
}
