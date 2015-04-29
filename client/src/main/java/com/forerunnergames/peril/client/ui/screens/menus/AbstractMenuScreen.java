package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.widgets.popup.Popup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractMenuScreen extends InputAdapter implements Screen
{
  private final MenuScreenWidgetFactory widgetFactory;
  private final ScreenChanger screenChanger;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final Table menuChoiceTable;
  private final Cell <Actor> titleBackgroundCell;
  private final Cell titleHeightCell;

  protected AbstractMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    this.widgetFactory = widgetFactory;
    this.screenChanger = screenChanger;

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport);

    // Layer 0 - screen background
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createScreenBackground ());

    // Layer 1 - right background shadow
    final Table tableL1 = new Table ().top ().left ();
    tableL1.add ().width (660);
    tableL1.add (widgetFactory.createRightBackgroundShadow ()).expandY ().fill ();
    rootStack.add (tableL1);

    // Layer 2 - top & bottom background shadows
    final Table tableL2 = new Table ().top ().left ();
    tableL2.add ().width (660);
    tableL2.add (widgetFactory.createTopBackgroundShadow ()).fill ();
    tableL2.row ();
    tableL2.add ().colspan (2).expandY ();
    tableL2.row ();
    tableL2.add ();
    tableL2.add (widgetFactory.createBottomBackgroundShadow ()).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    final Table titleBackgroundTable = new Table ().top ().left ();
    titleBackgroundTable.add ().width (301).height (400);
    titleBackgroundTable.row ();
    titleBackgroundTable.add ();
    titleBackgroundCell = titleBackgroundTable.add (widgetFactory.createTitleBackground ()).size (358, 60).fill ();
    rootStack.add (titleBackgroundTable);

    // Layer 4 - title text & choices
    menuChoiceTable = new Table ().top ().left ();
    menuChoiceTable.add ().width (301).height (400);
    menuChoiceTable.row ();
    titleHeightCell = menuChoiceTable.add ().height (60);
    addTitle ();
    addMenuChoices ();
    addButtons ();
    rootStack.add (menuChoiceTable);

    // Layer 5 - left & right menu bar shadows
    final Table tableL5 = new Table ().top ().left ();
    tableL5.add ().width (300);
    tableL5.add (widgetFactory.createLeftMenuBarShadow ()).expandY ().fill ();
    tableL5.add ().width (316);
    tableL5.add (widgetFactory.createRightMenuBarShadow ()).expandY ().fill ();
    tableL5.setTouchable (Touchable.disabled);
    rootStack.add (tableL5);

    stage.addActor (rootStack);

    inputProcessor = new InputMultiplexer (stage, this);
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

  @Override
  public boolean keyDown (int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.ESCAPE:
      {
        onEscape ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  protected abstract void addTitle ();

  protected final void addTitleWithoutSubtitle (final String titleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");

    addTitle (titleText, 60);
    addTitleMenuChoicesSpacer (32);
  }

  protected final void addTitleWithSubtitle (final String titleText, final String subtitleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");
    Arguments.checkIsNotNullOrEmptyOrBlank (subtitleText, "subtitleText");

    titleBackgroundCell.height (80);

    addTitle (titleText, 50);
    addSubtitle (subtitleText);
    addTitleMenuChoicesSpacer (12);
  }

  protected abstract void addMenuChoices ();

  protected abstract void onEscape ();

  protected final void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    screenChanger.toScreen (id);
  }

  protected final void addMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNull (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    menuChoiceTable.row ();
    menuChoiceTable.add ().height (10);
    menuChoiceTable.row ();
    menuChoiceTable.add ();
    menuChoiceTable.add (widgetFactory.createMenuChoice (choiceText, listener)).width (358).height (40).left ()
            .fill ();
  }

  protected final Popup createQuitPopup (final String message, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (listener, "listener");

    return widgetFactory.createQuitPopup (message, stage, listener);
  }

  protected void addButtons ()
  {
  }

  protected final void addBackButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    menuChoiceTable.row ();
    menuChoiceTable.add ().height (388);
    menuChoiceTable.row ();
    menuChoiceTable.add ();
    menuChoiceTable.add (widgetFactory.createBackButton (listener)).width (110).left ().padLeft (59);
  }

  private void addTitle (final String text, final int height)
  {
    titleHeightCell.height (height);
    menuChoiceTable.add (widgetFactory.createTitle (text)).padLeft (30).height (37).top ().left ();
  }

  private void addSubtitle (final String subtitleText)
  {
    menuChoiceTable.row ();
    menuChoiceTable.add ().height (30);
    menuChoiceTable.add (widgetFactory.createSubTitle (subtitleText)).padLeft (30).top ().left ();
  }

  private void addTitleMenuChoicesSpacer (final int height)
  {
    menuChoiceTable.row ();
    menuChoiceTable.add ().height (height);
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
