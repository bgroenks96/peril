package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;

public final class MultiplayerClassicGameModeMenuScreen extends InputAdapter implements Screen
{
  private final ScreenChanger screenChanger;
  private final Stage stage;
  private final InputProcessor inputProcessor;

  public MultiplayerClassicGameModeMenuScreen (final ScreenChanger screenChanger, final ScreenSize screenSize, final Skin skin)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (skin, "skin");

    this.screenChanger = screenChanger;

    // Layer 0 - background image
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.menuAtlas.findRegion ("menuBackground")));

    // Layer 1 - right background shadow
    final Table tableL1 = new Table ().top ().left ();
    tableL1.add ().width (660);
    tableL1.add (new Image (Assets.menuAtlas.findRegion ("rightBackgroundShadow"))).width (32).expandY ().fill ();
    rootStack.add (tableL1);

    // Layer 2 - top & bottom background shadows
    final Table tableL2 = new Table ().top ().left ();
    tableL2.add ().width (660);

    final Sprite topAndBottomBackgroundShadow = Assets.menuAtlas.createSprite ("topAndBottomBackgroundShadow");

    tableL2.add (new Image (new SpriteDrawable (topAndBottomBackgroundShadow))).width (332).height (302).fill ();
    tableL2.row ();
    tableL2.add ().colspan (2).expandY ();
    tableL2.row ();
    tableL2.add ();

    final Sprite bottomBackgroundShadow = new Sprite (topAndBottomBackgroundShadow);
    bottomBackgroundShadow.flip (true, false);

    tableL2.add (new Image (new SpriteDrawable (bottomBackgroundShadow))).width (332).height (302).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    final Table tableL3 = new Table ().top ().left ();
    tableL3.add ().width (301).height (400);
    tableL3.row ();
    tableL3.add ();
    tableL3.add (new Image (new NinePatchDrawable (Assets.menuAtlas.createPatch ("menuTitleBackground"))))
            .size (358, 80).fill ();
    rootStack.add (tableL3);

    // Layer 4 - text & buttons
    final Table tableL4 = new Table ().top ().left ();
    tableL4.add ().width (301).height (400);
    tableL4.row ();
    tableL4.add ().height (50);
    tableL4.add (new Label ("MULTIPLAYER", new Label.LabelStyle (Assets.skyHookMono31, Color.WHITE))).padLeft (30)
            .height (37).top ().left ();
    tableL4.row ();
    tableL4.add ().height (30);
    tableL4.add (new Label ("CLASSIC MODE", new Label.LabelStyle (Assets.aurulentSans16, Color.WHITE))).padLeft (30)
            .top ().left ();
    tableL4.row ();
    tableL4.add ().height (22);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton.ImageTextButtonStyle menuChoiceButtonStyle = new ImageTextButton.ImageTextButtonStyle ();
    menuChoiceButtonStyle.over = new SpriteDrawable (Assets.menuAtlas.createSprite ("menuChoiceOver"));
    menuChoiceButtonStyle.font = Assets.droidSansMono18;

    final ImageTextButton createGameButton = new ImageTextButton ("CREATE GAME", menuChoiceButtonStyle);
    final Stack classicGameModeButtonStack = new Stack ();
    classicGameModeButtonStack.add (new Container<> (createGameButton.getLabel ()).left ().padLeft (60));
    classicGameModeButtonStack.add (createGameButton.getImage ());
    createGameButton.clearChildren ();
    createGameButton.add (classicGameModeButtonStack).fill ().expand ();
    createGameButton.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        screenChanger.toScreen (ScreenId.PLAY_CLASSIC);
      }
    });

    tableL4.add (createGameButton).width (358).height (40).left ().fill ();
    tableL4.row ();
    tableL4.add ().height (10);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton joinGameButton = new ImageTextButton ("JOIN GAME", menuChoiceButtonStyle);
    final Stack perilGameModeButtonStack = new Stack ();
    perilGameModeButtonStack.add (new Container<> (joinGameButton.getLabel ()).left ().padLeft (60));
    perilGameModeButtonStack.add (joinGameButton.getImage ());
    joinGameButton.clearChildren ();
    joinGameButton.setSize (358, 40);
    joinGameButton.add (perilGameModeButtonStack).fill ().expand ();
    joinGameButton.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        screenChanger.toScreen (ScreenId.PLAY_CLASSIC);
      }
    });

    tableL4.add (joinGameButton).width (358).height (40).left ().fill ();

    tableL4.row ();
    tableL4.add ().height (388);
    tableL4.row ();
    tableL4.add ();

    final TextButton backButton = new TextButton ("BACK", skin);
    backButton.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        screenChanger.toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);
      }
    });

    tableL4.add (backButton).width (110).left ().padLeft (59);

    rootStack.add (tableL4);

    // Layer 5 - left & right menu bar shadows
    final Table tableL5 = new Table ().top ().left ();
    tableL5.add ().width (300);

    final TextureRegion leftAndRightMenuBarShadow = Assets.menuAtlas.findRegion ("leftAndRightMenuBarShadow");

    tableL5.add (new Image (leftAndRightMenuBarShadow)).width (22).expandY ().fill ();
    tableL5.add ().width (316);

    final TextureRegion rightMenuBarShadow = new TextureRegion (leftAndRightMenuBarShadow);
    rightMenuBarShadow.flip (true, false);

    tableL5.add (new Image (rightMenuBarShadow)).width (22).expandY ().fill ();
    tableL5.setTouchable (Touchable.disabled);
    rootStack.add (tableL5);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport);
    stage.addActor (rootStack);

    inputProcessor = new InputMultiplexer (stage, this);
  }

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.ESCAPE:
      {
        screenChanger.toScreen (ScreenId.MULTIPLAYER_GAME_MODES_MENU);

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

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (Gdx.input.getX (), Gdx.input.getY ());
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
    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();
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
