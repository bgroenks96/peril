/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.menus.modes.classic;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ClassicGameModeMenuScreen extends AbstractMenuScreen
{
  public ClassicGameModeMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                                    final ScreenChanger screenChanger,
                                    final ScreenSize screenSize,
                                    final MouseInput mouseInput,
                                    final Batch batch,
                                    final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    addTitle ("CLASSIC MODE", Align.left, 60);

    addMenuChoiceSpacer (42);

    addMenuChoice ("CREATE GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.CLASSIC_GAME_MODE_CREATE_GAME_MENU);
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("JOIN GAME", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.CLASSIC_GAME_MODE_JOIN_GAME_MENU);
      }
    });

    addBackButton (new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.GAME_MODES_MENU);
      }
    });
  }

  @Override
  protected boolean onEscape ()
  {
    if (!super.onEscape ()) toScreen (ScreenId.GAME_MODES_MENU);
    return true;
  }
}
