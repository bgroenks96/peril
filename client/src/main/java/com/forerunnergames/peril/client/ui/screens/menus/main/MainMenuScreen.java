package com.forerunnergames.peril.client.ui.screens.menus.main;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.widgets.Popup;
import com.forerunnergames.peril.client.ui.widgets.PopupStyle;
import com.forerunnergames.peril.client.ui.widgets.QuitPopup;
import com.forerunnergames.tools.common.Arguments;

public final class MainMenuScreen extends InputAdapter implements Screen
{
  private final ScreenMusic music;
  private final Stage stage;
  private final Popup quitPopup;
  private final InputProcessor inputProcessor;

  public MainMenuScreen (final ScreenController screenController,
                         final ScreenSize screenSize,
                         final ScreenMusic music,
                         final Skin skin)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (skin, "skin");

    this.music = music;

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
    tableL2.add (new Image (new TiledDrawable (Assets.menuAtlas.findRegion ("topBackgroundShadow")))).width (332)
            .height (302).fill ();
    tableL2.row ();
    tableL2.add ().colspan (2).expandY ();
    tableL2.row ();
    tableL2.add ();
    tableL2.add (new Image (new TiledDrawable (Assets.menuAtlas.findRegion ("bottomBackgroundShadow")))).width (332)
            .height (302).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    final Table tableL3 = new Table ().top ().left ();
    tableL3.add ().width (301).height (400);
    tableL3.row ();
    tableL3.add ();
    tableL3.add (new Image (new NinePatchDrawable (Assets.menuAtlas.createPatch ("menuTitleBackground"))))
            .size (358, 60).fill ();
    rootStack.add (tableL3);

    // Layer 4 - text & buttons
    final Table tableL4 = new Table ().top ().left ();
    tableL4.add ().width (301).height (400);
    tableL4.row ();
    tableL4.add ().height (60);
    tableL4.add (new Label ("Main Menu", new Label.LabelStyle (Assets.skyHookMono31, Color.WHITE))).padLeft (30)
            .height (37).top ().left ();
    tableL4.row ();
    tableL4.add ().height (42);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle ();
    buttonStyle.over = new SpriteDrawable (Assets.menuAtlas.createSprite ("menuChoiceOver"));
    buttonStyle.font = Assets.droidSansMono18;

    final ImageTextButton singlePlayerButton = new ImageTextButton ("Single Player", buttonStyle);
    final Stack singlePlayerButtonStack = new Stack ();
    singlePlayerButtonStack.add (new Container <> (singlePlayerButton.getLabel ()).left ().padLeft (60));
    singlePlayerButtonStack.add (singlePlayerButton.getImage ());
    singlePlayerButton.clearChildren ();
    singlePlayerButton.add (singlePlayerButtonStack).fill ().expand ();

    tableL4.add (singlePlayerButton).width (358).height (40).left ().fill ();
    tableL4.row ();
    tableL4.add ().height (10);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton multiplayerPlayerButton = new ImageTextButton ("Multiplayer", buttonStyle);
    final Stack multiplayerButtonStack = new Stack ();
    multiplayerButtonStack.add (new Container <> (multiplayerPlayerButton.getLabel ()).left ().padLeft (60));
    multiplayerButtonStack.add (multiplayerPlayerButton.getImage ());
    multiplayerPlayerButton.clearChildren ();
    multiplayerPlayerButton.setSize (358, 40);
    multiplayerPlayerButton.add (multiplayerButtonStack).fill ().expand ();
    multiplayerPlayerButton.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        screenController.toScreen (ScreenId.PLAY_CLASSIC);
      }
    });

    tableL4.add (multiplayerPlayerButton).width (358).height (40).left ().fill ();
    tableL4.row ();
    tableL4.add ().height (10);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton settingsButton = new ImageTextButton ("Settings", buttonStyle);
    final Stack settingsButtonStack = new Stack ();
    settingsButtonStack.add (new Container <> (settingsButton.getLabel ()).left ().padLeft (60));
    settingsButtonStack.add (settingsButton.getImage ());
    settingsButton.clearChildren ();
    settingsButton.add (settingsButtonStack).fill ().expand ();

    tableL4.add (settingsButton).width (358).height (40).left ().fill ();
    tableL4.row ();
    tableL4.add ().height (10);
    tableL4.row ();
    tableL4.add ();

    final ImageTextButton quitButton = new ImageTextButton ("Quit", buttonStyle);
    final Stack quitButtonStack = new Stack ();
    quitButtonStack.add (new Container <> (quitButton.getLabel ()).left ().padLeft (60));
    quitButtonStack.add (quitButton.getImage ());
    quitButton.clearChildren ();
    quitButton.add (quitButtonStack).fill ().expand ();
    quitButton.addListener (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        quitPopup.show ();
      }
    });

    tableL4.add (quitButton).width (358).height (40).left ().fill ();

    rootStack.add (tableL4);

    // Layer 5 - left & right menu bar shadows
    final Table tableL5 = new Table ().top ().left ();
    tableL5.add ().width (300);
    tableL5.add (new Image (Assets.menuAtlas.findRegion ("leftMenuBarShadow"))).width (22).expandY ().fill ();
    tableL5.add ().width (316);
    tableL5.add (new Image (Assets.menuAtlas.findRegion ("rightMenuBarShadow"))).width (22).expandY ().fill ();
    tableL5.setTouchable (Touchable.disabled);
    rootStack.add (tableL5);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport);
    stage.addActor (rootStack);

    quitPopup = new QuitPopup (skin, PopupStyle.builder ().titleHeight (34).message ("Are you sure you want to quit?")
            .build (), stage)
    {
      @Override
      public void onSubmit ()
      {
        Gdx.app.exit ();
      }
    };

    inputProcessor = new InputMultiplexer (stage, this);
  }

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.ESCAPE:
      {
        quitPopup.show ();

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

    music.start ();
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

    music.stop ();

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
