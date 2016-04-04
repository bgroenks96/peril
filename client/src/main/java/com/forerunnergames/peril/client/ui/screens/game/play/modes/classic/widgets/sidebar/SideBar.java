/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.sidebar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

public final class SideBar extends Table
{
  private static final int SIDEBAR_INNER_PADDING_TOP = 20;
  private static final int SIDEBAR_INNER_PADDING_LEFT = 20;
  private static final int SIDEBAR_INNER_PADDING_RIGHT = 20;
  private static final int BUTTON_WIDTH = 40;
  private static final int BUTTON_HEIGHT = 40;
  private static final int VERTICAL_PADDING_BETWEEN_BUTTONS = 20;
  private final ImmutableSet <SideBarButton> buttons;
  private final ClassicModePlayScreenWidgetFactory widgetFactory;

  public SideBar (final ClassicModePlayScreenWidgetFactory widgetFactory, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;

    top ().padTop (SIDEBAR_INNER_PADDING_TOP).padLeft (SIDEBAR_INNER_PADDING_LEFT)
            .padRight (SIDEBAR_INNER_PADDING_RIGHT);

    // @formatter:off

    final SideBarButton tradeInButton =
            widgetFactory.createSideBarButton (SideBarButton.ButtonType.TRADE_IN,
                    new ClickListener (Input.Buttons.LEFT)
                    {
                      @Override
                      public void clicked (final InputEvent event, final float x, final float y)
                      {
                      }
                    });

    add (tradeInButton.asActor ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    final SideBarButton reinforceButton =
            widgetFactory.createSideBarButton (SideBarButton.ButtonType.REINFORCE,
                    new ClickListener (Input.Buttons.LEFT)
                    {
                      @Override
                      public void clicked (final InputEvent event, final float x, final float y)
                      {
                      }
                    });

    add (reinforceButton.asActor ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    final SideBarButton endTurnButton =
            widgetFactory.createSideBarButton (SideBarButton.ButtonType.END_TURN,
                    new ClickListener (Input.Buttons.LEFT)
                    {
                      @Override
                      public void clicked (final InputEvent event, final float x, final float y)
                      {
                      }
                    });

    add (endTurnButton.asActor ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    row ().padTop (VERTICAL_PADDING_BETWEEN_BUTTONS);

    final SideBarButton mySettingsButton =
            widgetFactory.createSideBarButton (SideBarButton.ButtonType.MY_SETTINGS,
                    new ClickListener (Input.Buttons.LEFT)
                    {
                      @Override
                      public void clicked (final InputEvent event, final float x, final float y)
                      {
                      }
                    });

    add (mySettingsButton.asActor ()).top ().width (BUTTON_WIDTH).height (BUTTON_HEIGHT);

    // @formatter:on

    buttons = ImmutableSet.of (tradeInButton, reinforceButton, endTurnButton, mySettingsButton);
  }

  public void refreshAssets ()
  {
    for (final SideBarButton button : buttons)
    {
      button.setStyle (widgetFactory.createSideBarButtonStyle (button.getType ()));
    }
  }
}
