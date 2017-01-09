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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.AbstractArmyMovementDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.KeyListener;
import com.forerunnergames.tools.common.Arguments;

public final class FortificationDialog extends AbstractArmyMovementDialog implements CancellableDialog
{
  private static final String TITLE = "POST-COMBAT MANEUVER";
  private static final String CANCEL_BUTTON_TEXT = "CANCEL";
  private final CancellableDialogListener listener;
  private TextButton cancelButton;

  public FortificationDialog (final ClassicModePlayScreenWidgetFactory widgetFactory,
                              final Stage stage,
                              final CancellableDialogListener listener)

  {
    super (widgetFactory, TITLE, stage, listener);

    Arguments.checkIsNotNull (listener, "listener");

    this.listener = listener;
  }

  @Override
  protected void addButtons ()
  {
    cancelButton = addTextButton (CANCEL_BUTTON_TEXT, DialogAction.HIDE, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        listener.onCancel ();
      }
    });

    super.addButtons ();
  }

  @Override
  protected void addKeys ()
  {
    super.addKeys ();

    addKey (Input.Keys.ESCAPE, DialogAction.HIDE, new KeyListener ()
    {
      @Override
      public void keyDown ()
      {
        cancelButton.toggle ();
      }
    });
  }
}
