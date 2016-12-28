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

import com.forerunnergames.peril.client.events.ReJoinGameEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.io.CachedGameSessionManager;
import com.forerunnergames.peril.client.io.CachedGameSessionManager.CachedGameSession;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.menus.AbstractMenuScreen;
import com.forerunnergames.peril.client.ui.screens.menus.MenuScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;

import net.engio.mbassy.bus.MBassador;

public final class MainMenuScreen extends AbstractMenuScreen
{
  private static final String QUIT_DIALOG_MESSAGE = "Are you sure you want to quit Peril?";
  private static final String RECONNECTION_DIALOG_TITLE = "REJOIN GAME?";
  private static final String RECONNECTION_DIALOG_MESSAGE = "It looks like you were in the middle of a game! Would you like to rejoin it?";
  private final Dialog errorDialog;
  private final Dialog quitDialog;
  private final Dialog reconnectionDialog;

  public MainMenuScreen (final MenuScreenWidgetFactory widgetFactory,
                         final ScreenChanger screenChanger,
                         final ScreenSize screenSize,
                         final MouseInput mouseInput,
                         final Batch batch,
                         final MBassador <Event> eventBus)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);
    errorDialog = createErrorDialog ();
    quitDialog = createQuitDialog (QUIT_DIALOG_MESSAGE, new QuitDialogListener ());
    reconnectionDialog = createConfirmationDialog (RECONNECTION_DIALOG_TITLE, RECONNECTION_DIALOG_MESSAGE,
                                                   new ReconnectionDialogListener ());

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
    super.show ();
    if (CachedGameSessionManager.existsSession ()) reconnectionDialog.show ();
  }

  @Override
  protected boolean onEscape ()
  {
    if (!super.onEscape ()) quitDialog.show ();
    return true;
  }

  @Override
  protected void update (final float delta)
  {
    super.update (delta);
    quitDialog.update (delta);
  }

  private static final class QuitDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onSubmit ()
    {
      Gdx.app.exit ();
    }
  }

  private final class ReconnectionDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onCancel ()
    {
      // You only get one chance to rejoin a game.
      CachedGameSessionManager.deleteSession ();
    }

    @Override
    public void onSubmit ()
    {
      final DataResult <CachedGameSession, Exception> result = CachedGameSessionManager.loadSession ();
      CachedGameSessionManager.deleteSession ();

      if (result.failed ())
      {
        errorDialog
                .setMessage (new DefaultMessage (
                        Strings.format ("An error occurred while attempting to rejoin the game.\n\n"
                                + "Problem:\n\n{}\n\nDetails:\n\n{}",
                                        Throwables.getRootCause (result.getFailureReason ()).getMessage (),
                                        Strings.toString (result.getFailureReason ()))));
        return;
      }

      toScreen (ScreenId.MENU_TO_PLAY_LOADING);

      // The menu-to-play loading screen is now active & can therefore receive events.

      final CachedGameSession gameSession = result.getReturnValue ();

      publishAsync (new ReJoinGameEvent (gameSession.getPlayerName (), gameSession.getServerAddress (),
              gameSession.getPlayerSecretId ()));
    }
  }
}
