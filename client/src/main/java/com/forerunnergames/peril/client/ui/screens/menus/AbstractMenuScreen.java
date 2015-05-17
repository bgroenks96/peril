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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractMenuScreen extends InputAdapter implements Screen
{
  private static final Interpolation menuBarTransitionInterpolation = Interpolation.pow2;
  private static final float menuBarTransitionTimeSeconds = 0.5f;
  private final Collection <Cell <Actor>> menuChoiceActorCells = new ArrayList <> ();
  private final MenuScreenWidgetFactory widgetFactory;
  private final ScreenChanger screenChanger;
  private final Actor menuBarActor;
  private final Actor rightBackgroundShadowActor;
  private final Actor titleBackgroundActor;
  private final Actor rightMenuBarShadowActor;
  private final Stage stage;
  private final InputProcessor inputProcessor;
  private final Table interactionTable;
  private final Table titleTable;
  private final Table menuChoicesTable;
  private final Table buttonTable;
  private final Cell <Actor> titleBackgroundCell;
  private final Cell <Actor> contentActorCell;
  private final Cell <Table> titlesTableCell;
  private boolean screenTransitionInProgress = false;
  private boolean menuBarTransitionInProgress = false;
  private MenuBarState currentMenuBarState = MenuBarState.CONTRACTED;

  protected AbstractMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize,
                                final Batch batch)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (batch, "batch");

    this.widgetFactory = widgetFactory;
    this.screenChanger = screenChanger;

    menuBarActor = MenuScreenWidgetFactory.createMenuBar ();
    rightBackgroundShadowActor = MenuScreenWidgetFactory.createRightBackgroundShadow ();
    titleBackgroundActor = MenuScreenWidgetFactory.createTitleBackground ();
    rightMenuBarShadowActor = widgetFactory.createRightMenuBarShadow ();

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport, batch);

    // Layer 0 - screen background
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    final Table tableL0 = new Table ().top ().left ();
    tableL0.add (MenuScreenWidgetFactory.createScreenBackgroundLeft ());
    tableL0.add ().expandX ();
    tableL0.add (MenuScreenWidgetFactory.createScreenBackgroundRight ());
    rootStack.add (tableL0);

    // Layer 1 - menu bar & right background shadow
    final Table tableL1 = new Table ().top ().left ();
    tableL1.add ().width (302);
    tableL1.add (menuBarActor).width (MenuBarState.CONTRACTED.getWidth ()).expandY ().fillY ();
    tableL1.add (rightBackgroundShadowActor).expandY ().fill ();
    rootStack.add (tableL1);

    // Layer 2 - top & bottom background shadows
    final Table tableL2 = new Table ().top ().left ();
    tableL2.add ().width (300);
    tableL2.add (widgetFactory.createTopBackgroundShadow ()).size (692, 302).fill ();
    tableL2.row ();
    tableL2.add ().expandY ();
    tableL2.row ();
    tableL2.add ();
    tableL2.add (widgetFactory.createBottomBackgroundShadow ()).size (692, 302).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    final Table tableL3 = new Table ().top ().left ();
    tableL3.add ().width (301).height (400);
    tableL3.row ();
    tableL3.add ();
    titleBackgroundCell = tableL3.add (titleBackgroundActor).width (currentMenuBarState.getWidth ()).height (0).fill ();
    rootStack.add (tableL3);

    // Layer 4 - title text, menu choices, & buttons
    interactionTable = new Table ().top ().left ();
    interactionTable.add ().width (301).height (400);
    interactionTable.row ();
    interactionTable.add ();
    titleTable = new Table ().left ();
    titlesTableCell = interactionTable.add (titleTable).padLeft (30).width (currentMenuBarState.getWidth () - 30)
            .height (0).left ().fill ();
    interactionTable.row ();
    interactionTable.add ();
    menuChoicesTable = new Table ().top ().left ();
    interactionTable.add (menuChoicesTable);
    interactionTable.row ();
    interactionTable.add ();
    contentActorCell = interactionTable.add ((Actor) null).expandY ().fill ();
    interactionTable.row ();
    interactionTable.add ();
    buttonTable = new Table ().bottom ();
    interactionTable.add (buttonTable).padLeft (60).padRight (60).padBottom (60).fill ();
    rootStack.add (interactionTable);

    // Layer 5 - left & right menu bar shadows
    final Table tableL5 = new Table ().top ().left ();
    tableL5.add ().width (300);
    tableL5.add (widgetFactory.createLeftMenuBarShadow ()).expandY ().fill ();
    tableL5.add ().width (MenuBarState.CONTRACTED.getWidth () - 42);
    tableL5.add (rightMenuBarShadowActor).expandY ().fill ();
    tableL5.setTouchable (Touchable.disabled);
    rootStack.add (tableL5);

    stage.addActor (rootStack);

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

    inputProcessor = new InputMultiplexer (stage, new InputAdapter ()
    {
      @Override
      public boolean keyDown (final int keycode)
      {
        switch (keycode)
        {
          case Input.Keys.ESCAPE:
          {
            if (screenTransitionInProgress || menuBarTransitionInProgress) return true;

            onEscape ();

            return true;
          }
          default:
          {
            return false;
          }
        }
      }
    });
  }

  private enum MenuBarState
  {
    CONTRACTED (358),
    EXPANDED (658);

    private final int width;

    MenuBarState (final int width)
    {
      this.width = width;
    }

    public int getWidth ()
    {
      return width;
    }

    public boolean is (final MenuBarState menuBarState)
    {
      return this == menuBarState;
    }

    public boolean isNot (final MenuBarState menuBarState)
    {
      return !is (menuBarState);
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

  protected final void addTitle (final String text, final int alignment, final int height)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNegative (height, "height");

    titleTable.row ();
    titleTable.add (widgetFactory.createTitle (text, alignment)).expandX ().height (height).fill ().align (alignment);
    titlesTableCell.height (titlesTableCell.getPrefHeight () + height);
    titleBackgroundCell.height (titleBackgroundCell.getPrefHeight () + height);
  }

  protected final void addTitleSpacer (final int height)
  {
    titleTable.row ();
    titleTable.add ().height (height);
    titlesTableCell.height (titlesTableCell.getPrefHeight () + height);
    titleBackgroundCell.height (titleBackgroundCell.getPrefHeight () + height);
  }

  protected final void addContent (final Actor content)
  {
    Arguments.checkIsNotNull (content, "content");

    contentActorCell.setActor (content);
  }

  protected abstract void onEscape ();

  protected final void addMenuChoiceSpacer (final int height)
  {
    Arguments.checkIsNotNegative (height, "height");

    menuChoicesTable.row ();
    menuChoicesTable.add ().height (height);
    menuChoicesTable.layout ();
  }

  protected final void addMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNull (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    menuChoiceActorCells.add (menuChoicesTable.add (widgetFactory.createMenuChoice (choiceText, listener))
            .size (currentMenuBarState.getWidth (), 40).left ().fill ());
  }

  protected final void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (screenTransitionInProgress)
    {
      return;
    }

    screenTransitionInProgress = true;
    screenChanger.toScreen (id);
    screenTransitionInProgress = false;
  }

  protected final void expandMenuBar ()
  {
    expandMenuBar (new Runnable ()
    {
      @Override
      public void run ()
      {
      }
    });
  }

  // @formatter:off
  protected final void expandMenuBar (final Runnable completionRunnable)
  {
    Arguments.checkIsNotNull (completionRunnable, "completionRunnable");

    if (currentMenuBarState.is (MenuBarState.EXPANDED) || menuBarTransitionInProgress) return;

    currentMenuBarState = MenuBarState.EXPANDED;
    menuBarTransitionInProgress = true;

    interactionTable.setTouchable (Touchable.disabled);
    interactionTable.setVisible (false);

    titlesTableCell.width (MenuBarState.EXPANDED.getWidth () - 30);

    for (final Cell <Actor> menuChoiceActorCell : menuChoiceActorCells)
    {
      menuChoiceActorCell.width (MenuBarState.EXPANDED.getWidth ());
    }

    interactionTable.invalidate ();

    menuBarActor.addAction (
            Actions.sizeBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    titleBackgroundActor.addAction (
            Actions.sizeBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    rightMenuBarShadowActor.addAction (
            Actions.moveBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    rightBackgroundShadowActor.addAction (
            Actions.sequence (
                    Actions.moveBy (
                            MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                            menuBarTransitionTimeSeconds, menuBarTransitionInterpolation),
                    Actions.run (new Runnable ()
                    {
                      @Override
                      public void run ()
                      {
                        interactionTable.setVisible (true);
                        interactionTable.setTouchable (Touchable.enabled);
                        menuBarTransitionInProgress = false;
                      }
                    }),
                    Actions.run (completionRunnable)));
  }
  // @formatter:on

  protected final void contractMenuBar ()
  {
    contractMenuBar (new Runnable ()
    {
      @Override
      public void run ()
      {
      }
    });
  }

  // @formatter:off
  protected final void contractMenuBar (final Runnable completionRunnable)
  {
    Arguments.checkIsNotNull (completionRunnable, "completionRunnable");

    Arguments.checkIsNotNull (completionRunnable, "completionRunnable");

    if (currentMenuBarState.is (MenuBarState.CONTRACTED) || menuBarTransitionInProgress) return;

    currentMenuBarState = MenuBarState.CONTRACTED;
    menuBarTransitionInProgress = true;

    interactionTable.setTouchable (Touchable.disabled);
    interactionTable.setVisible (false);

    menuBarActor.addAction (
            Actions.sizeBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    titleBackgroundActor.addAction (
            Actions.sizeBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    rightMenuBarShadowActor.addAction (
            Actions.moveBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    menuBarTransitionTimeSeconds, menuBarTransitionInterpolation));

    rightBackgroundShadowActor.addAction (
            Actions.sequence (
                    Actions.moveBy (
                            MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                            menuBarTransitionTimeSeconds, menuBarTransitionInterpolation),
                    Actions.run (new Runnable ()
                    {
                      @Override
                      public void run ()
                      {
                        menuBarTransitionInProgress = false;
                      }
                    }),
                    Actions.run (completionRunnable)));
  }
  // @formatter:on

  protected final Popup createQuitPopup (final String message, final PopupListener listener)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (listener, "listener");

    return widgetFactory.createQuitPopup (message, stage, listener);
  }

  protected final void addBackButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    buttonTable.add (widgetFactory.createTextButton ("BACK", listener)).width (110);
    buttonTable.add ().expandX ();
  }

  protected final void addForwardButton (final String text, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (text, "text");
    Arguments.checkIsNotNull (listener, "listener");

    buttonTable.add (widgetFactory.createTextButton (text, listener)).width (220);
  }

  private static void showCursor ()
  {
    Gdx.input.setCursorImage (Assets.menuNormalCursor, Math.round (InputSettings.MENU_NORMAL_MOUSE_CURSOR_HOTSPOT.x),
                              Math.round (InputSettings.MENU_NORMAL_MOUSE_CURSOR_HOTSPOT.y));
  }

  private static void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }
}
