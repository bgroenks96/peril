/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.menus;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.screens.AbstractScreen;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

public abstract class AbstractMenuScreen extends AbstractScreen
{
  private static final Interpolation MENU_BAR_TRANSITION_INTERPOLATION = Interpolation.pow2;
  private static final float MENU_BAR_TRANSITION_TIME_SECONDS = 0.5f;
  private static final String BACK_BUTTON_TEXT = "BACK";
  private final Collection <Cell <ImageTextButton>> menuChoiceCells = new ArrayList<> ();
  private final Collection <ImageTextButton> menuChoices = new ArrayList<> ();
  private final Multimap <String, TextButton> textButtonStyleNameToTextButtons = HashMultimap.create ();
  private final MenuScreenWidgetFactory widgetFactory;
  private final Image screenBackgroundLeft;
  private final Image screenBackgroundRight;
  private final Image menuBar;
  private final Image rightBackgroundShadow;
  private final Image topBackgroundShadow;
  private final Image bottomBackgroundShadow;
  private final Image titleBackground;
  private final Image leftMenuBarShadow;
  private final Image rightMenuBarShadow;
  private final Label titleLabel;
  private final Label subTitleLabel;
  private final Stack rootStack;
  private final Table tableL0;
  private final Table tableL1;
  private final Table tableL2;
  private final Table tableL3;
  private final Table tableL5;
  private final Table interactionTable;
  private final Table titleTable;
  private final Table menuChoicesTable;
  private final Table buttonTable;
  private final Cell <Image> titleBackgroundCell;
  private final Cell <Actor> contentActorCell;
  private final Cell <Table> titlesTableCell;
  private final AtomicBoolean menuBarTransitionInProgress = new AtomicBoolean ();
  private MenuBarState currentMenuBarState = MenuBarState.CONTRACTED;

  private enum MenuBarState
  {
    CONTRACTED (360),
    EXPANDED (660);

    private final int width;

    public int getWidth ()
    {
      return width;
    }

    public boolean is (final MenuBarState menuBarState)
    {
      return this == menuBarState;
    }

    MenuBarState (final int width)
    {
      this.width = width;
    }
  }

  protected AbstractMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize,
                                final MouseInput mouseInput,
                                final Batch batch,
                                final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (batch, "batch");

    this.widgetFactory = widgetFactory;

    screenBackgroundLeft = widgetFactory.createScreenBackgroundLeft ();
    screenBackgroundRight = widgetFactory.createScreenBackgroundRight ();
    menuBar = widgetFactory.createMenuBar ();
    rightBackgroundShadow = widgetFactory.createRightBackgroundShadow ();
    topBackgroundShadow = widgetFactory.createTopBackgroundShadow ();
    bottomBackgroundShadow = widgetFactory.createBottomBackgroundShadow ();
    titleBackground = widgetFactory.createTitleBackground ();
    leftMenuBarShadow = widgetFactory.createLeftMenuBarShadow ();
    rightMenuBarShadow = widgetFactory.createRightMenuBarShadow ();
    titleLabel = widgetFactory.createTitle ("", Align.left);
    subTitleLabel = widgetFactory.createSubTitle ("", Align.left);

    // Layer 0 - screen background
    rootStack = new Stack ();
    rootStack.setFillParent (true);
    tableL0 = new Table ().top ().left ();
    tableL0.add (screenBackgroundLeft);
    tableL0.add ().expandX ();
    tableL0.add (screenBackgroundRight);
    rootStack.add (tableL0);

    // Layer 1 - menu bar & right background shadow
    tableL1 = new Table ().top ().left ();
    tableL1.add ().width (300);
    tableL1.add (menuBar).width (currentMenuBarState.getWidth ()).fillX ().expandY ().fillY ();
    tableL1.add (rightBackgroundShadow).expandY ().fill ();
    rootStack.add (tableL1);

    // Layer 2 - top & bottom background shadows
    tableL2 = new Table ().top ().left ();
    tableL2.add ().width (300);
    tableL2.add (topBackgroundShadow).size (692, 302).fill ();
    tableL2.row ();
    tableL2.add ().expandY ();
    tableL2.row ();
    tableL2.add ();
    tableL2.add (bottomBackgroundShadow).size (692, 302).fill ();
    rootStack.add (tableL2);

    // Layer 3 - title background
    tableL3 = new Table ().top ().left ();
    tableL3.add ().width (301).height (400);
    tableL3.row ();
    tableL3.add ();
    titleBackgroundCell = tableL3.add (titleBackground).width (currentMenuBarState.getWidth () - 2).height (0).fill ();
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
    tableL5 = new Table ().top ().left ();
    tableL5.add ().width (300);
    tableL5.add (leftMenuBarShadow).expandY ().fill ();
    tableL5.add ().width (currentMenuBarState.getWidth () - 44);
    tableL5.add (rightMenuBarShadow).expandY ().fill ();
    tableL5.setTouchable (Touchable.disabled);
    rootStack.add (tableL5);

    addRootActor (rootStack);
  }

  @Override
  public void show ()
  {
    super.show ();

    screenBackgroundLeft.setDrawable (widgetFactory.createScreenBackgroundLeftDrawable ());
    screenBackgroundRight.setDrawable (widgetFactory.createScreenBackgroundRightDrawable ());
    menuBar.setDrawable (widgetFactory.createMenuBarDrawable ());
    rightBackgroundShadow.setDrawable (widgetFactory.createRightBackgroundShadowDrawable ());
    topBackgroundShadow.setDrawable (widgetFactory.createTopBackgroundShadowDrawable ());
    bottomBackgroundShadow.setDrawable (widgetFactory.createBottomBackgroundShadowDrawable ());
    titleBackground.setDrawable (widgetFactory.createTitleBackgroundDrawable ());
    leftMenuBarShadow.setDrawable (widgetFactory.createLeftMenuBarShadowDrawable ());
    rightMenuBarShadow.setDrawable (widgetFactory.createRightMenuBarShadowDrawable ());
    titleLabel.setStyle (widgetFactory.createTitleStyle ());
    subTitleLabel.setStyle (widgetFactory.createSubTitleStyle ());
    for (final ImageTextButton menuChoice : menuChoices)
    {
      menuChoice.setStyle (widgetFactory.createMenuChoiceStyle ());
    }
    for (final Map.Entry <String, TextButton> entry : textButtonStyleNameToTextButtons.entries ())
    {
      entry.getValue ().setStyle (widgetFactory.createTextButtonStyle (entry.getKey ()));
    }
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected boolean onEscape ()
  {
    return menuBarTransitionInProgress.get ();
  }

  protected final void addTitle (final String text, final int alignment, final int height)
  {
    Arguments.checkIsNotNull (text, "text");
    Arguments.checkIsNotNegative (alignment, "alignment");
    Arguments.checkIsNotNegative (height, "height");

    titleLabel.setText (text);
    titleLabel.setAlignment (alignment);

    titleTable.row ();
    titleTable.add (titleLabel).expandX ().height (height).fill ().align (alignment);
    titlesTableCell.height (titlesTableCell.getPrefHeight () + height);
    titleBackgroundCell.height (titleBackgroundCell.getPrefHeight () + height);
  }

  protected final void addSubTitle (final String text)
  {
    Arguments.checkIsNotNull (text, "text");

    final int mainTitlePaddingTop = 5;
    final int subtitleHeight = 30;
    final int subtitlePaddingTop = 4;
    final int subtitlePaddingBottom = 1;
    final int subtitleAlignment = Align.topLeft;

    subTitleLabel.setText (text);
    subTitleLabel.setAlignment (subtitleAlignment);

    // @formatter:off
    titleTable.row ();
    titleTable.add (subTitleLabel).expandX ().height (subtitleHeight).fill ().align (subtitleAlignment).padTop (subtitlePaddingTop).padBottom (subtitlePaddingBottom);
    titlesTableCell.height (titlesTableCell.getPrefHeight () + subtitleHeight + subtitlePaddingTop + subtitlePaddingBottom).padTop (mainTitlePaddingTop);
    titleBackgroundCell.height (titleBackgroundCell.getPrefHeight () + subtitleHeight + subtitlePaddingTop + subtitlePaddingBottom + mainTitlePaddingTop);
    // @formatter:on
  }

  protected final void addContent (final Actor content)
  {
    Arguments.checkIsNotNull (content, "content");

    contentActorCell.setActor (content);
  }

  protected final void addMenuChoiceSpacer (final int height)
  {
    Arguments.checkIsNotNegative (height, "height");

    menuChoicesTable.row ();
    menuChoicesTable.add ().height (height);
    menuChoicesTable.row ();
    menuChoicesTable.layout ();
  }

  protected final void addMenuChoice (final String choiceText, final EventListener listener)
  {
    Arguments.checkIsNotNull (choiceText, "choiceText");
    Arguments.checkIsNotNull (listener, "listener");

    final ImageTextButton menuChoice = widgetFactory.createMenuChoice (choiceText, listener);
    menuChoices.add (menuChoice);

    menuChoiceCells.add (menuChoicesTable.add (menuChoice).size (currentMenuBarState.getWidth (), 40).left ().fill ());
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

  protected final void expandMenuBar (final Runnable completionRunnable)
  {
    Arguments.checkIsNotNull (completionRunnable, "completionRunnable");

    if (currentMenuBarState.is (MenuBarState.EXPANDED) || menuBarTransitionInProgress.get ()) return;

    currentMenuBarState = MenuBarState.EXPANDED;
    menuBarTransitionInProgress.set (true);

    interactionTable.setTouchable (Touchable.disabled);
    interactionTable.setVisible (false);

    titlesTableCell.width (currentMenuBarState.getWidth () - 30);

    for (final Cell <ImageTextButton> menuChoiceCell : menuChoiceCells)
    {
      menuChoiceCell.width (currentMenuBarState.getWidth () - 2);
    }

    rootStack.invalidate ();
    tableL0.invalidate ();
    tableL1.invalidate ();
    tableL2.invalidate ();
    tableL3.invalidate ();
    interactionTable.invalidate ();
    tableL5.invalidate ();
    titleTable.invalidate ();
    menuChoicesTable.invalidate ();
    buttonTable.invalidate ();

    menuBar.addAction (
            Actions.sizeBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    titleBackground.addAction (
            Actions.sizeBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    rightMenuBarShadow.addAction (
            Actions.moveBy (
                    MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    rightBackgroundShadow.addAction (
            Actions.sequence (
                    Actions.moveBy (
                            MenuBarState.EXPANDED.getWidth () - MenuBarState.CONTRACTED.getWidth (), 0.0f,
                            MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION),
                    Actions.run (new Runnable ()
                    {
                      @Override
                      public void run ()
                      {
                        interactionTable.setVisible (true);
                        interactionTable.setTouchable (Touchable.enabled);
                        menuBarTransitionInProgress.set (false);
                      }
                    }),
                    Actions.run (completionRunnable)));
  }

  protected final void contractMenuBar (final Runnable completionRunnable)
  {
    Arguments.checkIsNotNull (completionRunnable, "completionRunnable");

    if (currentMenuBarState.is (MenuBarState.CONTRACTED) || menuBarTransitionInProgress.get ()) return;

    currentMenuBarState = MenuBarState.CONTRACTED;
    menuBarTransitionInProgress.set (true);

    interactionTable.setTouchable (Touchable.disabled);
    interactionTable.setVisible (false);

    menuBar.addAction (
            Actions.sizeBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    titleBackground.addAction (
            Actions.sizeBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    rightMenuBarShadow.addAction (
            Actions.moveBy (
                    MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                    MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION));

    rightBackgroundShadow.addAction (
            Actions.sequence (
                    Actions.moveBy (
                            MenuBarState.CONTRACTED.getWidth () - MenuBarState.EXPANDED.getWidth (), 0.0f,
                            MENU_BAR_TRANSITION_TIME_SECONDS, MENU_BAR_TRANSITION_INTERPOLATION),
                    Actions.run (new Runnable ()
                    {
                      @Override
                      public void run ()
                      {
                        menuBarTransitionInProgress.set (false);
                      }
                    }),
                    Actions.run (completionRunnable)));
  }

  // @formatter:on

  protected final Button addBackButton (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final String buttonStyleName = StyleSettings.MENU_BACK_TEXT_BUTTON_STYLE;
    final TextButton button = widgetFactory.createTextButton (BACK_BUTTON_TEXT, buttonStyleName, listener);
    buttonTable.add (button).width (110);
    buttonTable.add ().expandX ();
    textButtonStyleNameToTextButtons.put (buttonStyleName, button);

    return button;
  }

  protected Button addForwardButton (final String text, final EventListener listener)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (text, "text");
    Arguments.checkIsNotNull (listener, "listener");

    final String buttonStyleName = StyleSettings.MENU_FORWARD_TEXT_BUTTON_STYLE;
    final TextButton button = widgetFactory.createTextButton (text, buttonStyleName, listener);
    buttonTable.add (button).width (220);
    textButtonStyleNameToTextButtons.put (buttonStyleName, button);

    return button;
  }
}
