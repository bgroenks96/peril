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
  private static final int MAX_BUTTONS = 2;
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
  private final Cell <Actor> titleBackgroundCell;
  private final Cell <?> titleHeightCell;
  private final Cell <Actor> contentActorCell;
  private Cell <Actor> titleActorCell = new Cell <> ();
  private boolean isFirstMenuChoice = true;
  private Title title = Title.NONE;
  private boolean screenTransitionInProgress = false;
  private boolean menuBarTransitionInProgress = false;
  private MenuBarState currentMenuBarState = MenuBarState.CONTRACTED;

  protected AbstractMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    this.widgetFactory = widgetFactory;
    this.screenChanger = screenChanger;

    menuBarActor = widgetFactory.createMenuBar ();
    rightBackgroundShadowActor = widgetFactory.createRightBackgroundShadow ();
    titleBackgroundActor = widgetFactory.createTitleBackground ();
    rightMenuBarShadowActor = widgetFactory.createRightMenuBarShadow ();

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport);

    // Layer 0 - screen background
    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createScreenBackground ());

    // Layer 1 - menu bar & right background shadow
    final Table tableL1 = new Table ().top ().left ();
    tableL1.add ().width (302);
    tableL1.add (menuBarActor).width (MenuBarState.CONTRACTED.getWidth ()).expandY ().fillY ();
    tableL1.add (rightBackgroundShadowActor).expandY ().fill ();
    rootStack.add (tableL1);

    // Layer 2 - top & bottom background shadows
    final Table tableL2 = new Table ().top ().left ();
    tableL2.add ().width (300);
    tableL2.add (widgetFactory.createTopBackgroundShadow ()).fill ();
    tableL2.row ();
    tableL2.add ().colspan (2).expandY ();
    tableL2.row ();
    tableL2.add ();
    tableL2.add (widgetFactory.createBottomBackgroundShadow ()).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    final Table tableL3 = new Table ().top ().left ();
    tableL3.add ().width (301).height (400);
    tableL3.row ();
    tableL3.add ();
    titleBackgroundCell = tableL3.add (titleBackgroundActor).fill ();
    rootStack.add (tableL3);

    // Layer 4 - title text, menu choices, & buttons
    interactionTable = new Table ().top ().left ();
    interactionTable.add ().width (301).height (400);
    interactionTable.row ();
    titleHeightCell = interactionTable.add ().height (60);
    addTitle ();
    addMenuChoices ();
    interactionTable.row ();
    interactionTable.add ();
    contentActorCell = interactionTable.add ((Actor) null).expandY ().fill ().colspan (MAX_BUTTONS);
    interactionTable.row ();
    interactionTable.add ();
    addButtons ();
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

    // interactionTable.debug ();
    // rootStack.debug ();

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

  protected enum Title
  {
    NONE (0),
    NORMAL (42),
    SUBTITLE (22);

    private final int menuChoicesSpacerHeight;

    Title (final int menuChoicesSpacerHeight)
    {
      this.menuChoicesSpacerHeight = menuChoicesSpacerHeight;
    }

    public int getMenuChoicesSpacerHeight ()
    {
      return menuChoicesSpacerHeight;
    }

    public boolean is (final Title title)
    {
      return this == title;
    }

    public boolean isNot (final Title title)
    {
      return !is (title);
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

  protected abstract void addTitle ();

  protected final void addContent (final Actor content)
  {
    Arguments.checkIsNotNull (content, "content");

    contentActorCell.setActor (content);
  }

  protected final void addTitleWithoutSubtitle (final String titleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");
    Arguments.checkIsTrue (title.is (Title.NONE), "Title has already been added.");

    titleBackgroundCell.size (MenuBarState.CONTRACTED.getWidth (), 60);

    addTitle (titleText, 60);

    title = Title.NORMAL;
  }

  protected final void addTitleWithSubtitle (final String titleText, final String subtitleText)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (titleText, "titleText");
    Arguments.checkIsNotNullOrEmptyOrBlank (subtitleText, "subtitleText");
    Arguments.checkIsTrue (title.is (Title.NONE), "Title has already been added.");

    titleBackgroundCell.size (MenuBarState.CONTRACTED.getWidth (), 80);

    addTitle (titleText, 50);
    addSubtitle (subtitleText);

    title = Title.SUBTITLE;
  }

  protected abstract void onEscape ();

  protected void addMenuChoices ()
  {
  }

  protected final void addMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNull (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    if (isFirstMenuChoice)
    {
      addTitleMenuChoicesSpacer ();
    }
    else
    {
      addMenuChoiceSpacer ();
    }

    interactionTable.row ();
    interactionTable.add ();
    menuChoiceActorCells.add (interactionTable.add (widgetFactory.createMenuChoice (choiceText, listener))
            .size (currentMenuBarState.getWidth (), 40).left ().fill ().colspan (MAX_BUTTONS));

    isFirstMenuChoice = false;
  }

  protected final void toScreen (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (screenTransitionInProgress) return;

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

    if (currentMenuBarState.is (MenuBarState.EXPANDED)) return;

    currentMenuBarState = MenuBarState.EXPANDED;
    menuBarTransitionInProgress = true;

    interactionTable.setTouchable (Touchable.disabled);
    interactionTable.setVisible (false);

    titleActorCell.width (MenuBarState.EXPANDED.getWidth () - 30);

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

    if (currentMenuBarState.is (MenuBarState.CONTRACTED)) return;

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

  protected void addButtons ()
  {
  }

  protected final void addBackButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    interactionTable.add (widgetFactory.createTextButton ("BACK", listener)).width (110).left ().padLeft (59)
            .padBottom (60);
  }

  protected final void addForwardButton (final String text, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (text, "text");
    Arguments.checkIsNotNull (listener, "listener");

    interactionTable.add (widgetFactory.createTextButton (text, listener)).width (220).right ().padRight (60)
            .padBottom (60);
  }

  private void addMenuChoiceSpacer ()
  {
    interactionTable.row ();
    interactionTable.add ().height (10);
  }

  private void addTitle (final String text, final int height)
  {
    titleHeightCell.height (height);
    titleActorCell = interactionTable.add (widgetFactory.createTitle (text)).padLeft (30)
            .size (currentMenuBarState.getWidth () - 30, 37).top ().left ().fill ().colspan (MAX_BUTTONS);
  }

  private void addSubtitle (final String subtitleText)
  {
    interactionTable.row ();
    interactionTable.add ().height (30);
    interactionTable.add (widgetFactory.createSubTitle (subtitleText)).padLeft (30).top ().left ();
  }

  private void addTitleMenuChoicesSpacer ()
  {
    interactionTable.row ();
    interactionTable.add ().height (title.getMenuChoicesSpacerHeight ());
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
