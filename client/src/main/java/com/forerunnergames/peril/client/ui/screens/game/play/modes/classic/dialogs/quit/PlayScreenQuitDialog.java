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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.quit;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.PlayMapBlockingDialog;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.QuitDialog;

public final class PlayScreenQuitDialog extends QuitDialog implements PlayMapBlockingDialog
{
  private static final String QUIT_DIALOG_TITLE_GAME_NOT_IN_PROGRESS = "Quit?";
  private static final String QUIT_DIALOG_MESSAGE_GAME_NOT_IN_PROGRESS = "Are you sure you want to quit?\n"
          + "If you are the host, quitting will shut down the server for everyone.";
  private static final String QUIT_DIALOG_TITLE_GAME_IN_PROGRESS = "Surrender & Quit?";
  private static final String QUIT_DIALOG_MESSAGE_GAME_IN_PROGRESS = "Are you sure you want to surrender & quit?\n"
          + "If you are the host, quitting will shut down the server for everyone.";

  public PlayScreenQuitDialog (final WidgetFactory widgetFactory,
                               final String message,
                               final Stage stage,
                               final CancellableDialogListener listener)
  {
    this (widgetFactory, message, DialogStyle.AUTO_H_CENTER, DialogStyle.AUTO_V_CENTER, stage, listener);
  }

  public PlayScreenQuitDialog (final WidgetFactory widgetFactory,
                               final String message,
                               final int positionUpperLeftReferenceScreenSpaceX,
                               final int positionUpperLeftReferenceScreenSpacey,
                               final Stage stage,
                               final CancellableDialogListener listener)
  {
    super (widgetFactory, message, positionUpperLeftReferenceScreenSpaceX, positionUpperLeftReferenceScreenSpacey,
           stage, listener);
  }

  public void show (final boolean isGameInProgress)
  {
    setTitle (getQuitDialogTitle (isGameInProgress));
    show (getQuitDialogMessageText (isGameInProgress));
  }

  private String getQuitDialogTitle (final boolean isGameInProgress)
  {
    return isGameInProgress ? QUIT_DIALOG_TITLE_GAME_IN_PROGRESS : QUIT_DIALOG_TITLE_GAME_NOT_IN_PROGRESS;
  }

  private String getQuitDialogMessageText (final boolean isGameInProgress)
  {
    return isGameInProgress ? QUIT_DIALOG_MESSAGE_GAME_IN_PROGRESS : QUIT_DIALOG_MESSAGE_GAME_NOT_IN_PROGRESS;
  }
}
