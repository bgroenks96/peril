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

package com.forerunnergames.peril.client.ui.screens.menus.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.io.GameServerCacheManager;
import com.forerunnergames.peril.client.io.GameServerCacheManager.CachedGameSession;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class MainMenuScreen extends AbstractMenuScreen
{
  private final Dialog quitDialog;
  private final Dialog reconnectDialog;

  public MainMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                         final ScreenChanger screenChanger,
                         final ScreenSize screenSize,
                         final MouseInput mouseInput,
                         final Batch batch,
                         final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    quitDialog = createQuitDialog ("Are you sure you want to quit Peril?", new CancellableDialogListenerAdapter ()
    {
      @Override
      public void onSubmit ()
      {
        Gdx.app.exit ();
      }
    });

    reconnectDialog = createConfirmDialog ("RECONNECT?",
                                           "It looks like you were in the middle of a game! Would you like to return to the control room, general?",
                                           new CancellableDialogListenerAdapter ()
                                           {
                                             @Override
                                             public void onSubmit ()
                                             {
                                               final CachedGameSession session = GameServerCacheManager
                                                       .readFromCache ();

                                               GameServerCacheManager.deleteCache ();

                                               // TODO: launch loading screen with reconnect join game handling instead
                                               // of normal handling
                                             }
                                           });

    addTitle ("MAIN MENU", Align.left, 60);

    addMenuChoiceSpacer (42);

    addMenuChoice ("PLAY", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        toScreen (ScreenId.GAME_MODES_MENU);
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("SETTINGS", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement
      }
    });

    addMenuChoiceSpacer (10);

    addMenuChoice ("QUIT", new ClickListener (Input.Buttons.LEFT)
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y)
      {
        quitDialog.show ();
      }
    });
  }

  @Override
  public void show ()
  {
    if (GameServerCacheManager.existsCachedSession ())
    {
      reconnectDialog.show ();
    }

    super.show ();
  }

  @Override
  protected void update (final float delta)
  {
    super.update (delta);
    quitDialog.update (delta);
  }

  @Override
  protected boolean onEscape ()
  {
    if (!super.onEscape ()) quitDialog.show ();
    return true;
  }
}
