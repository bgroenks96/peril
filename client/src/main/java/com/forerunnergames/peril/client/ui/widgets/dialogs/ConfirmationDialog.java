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

package com.forerunnergames.peril.client.ui.widgets.dialogs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

/**
 * Simple message dialog for presenting important binary choices.<br/>
 * <br/>
 * Requires the user to choose either "YES" or "NO".<br/>
 * <br/>
 * There is no ESCAPE key shortcut for the "NO" choice because it is important for the user to acknowledge one of the
 * choices, in a scenario where choosing "NO" could have some permanent consequence.
 */
public final class ConfirmationDialog extends OkCancelDialog
{
  public ConfirmationDialog (final WidgetFactory widgetFactory,
                             final String title,
                             final String message,
                             final Stage stage,
                             final CancellableDialogListener listener)
  {
    this (widgetFactory, title, message, DialogStyle.AUTO_H_CENTER, DialogStyle.AUTO_V_CENTER, stage, listener);
  }

  public ConfirmationDialog (final WidgetFactory widgetFactory,
                             final String title,
                             final String message,
                             final int positionUpperLeftReferenceScreenSpaceX,
                             final int positionUpperLeftReferenceScreenSpacey,
                             final Stage stage,
                             final CancellableDialogListener listener)
  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
                    .position (positionUpperLeftReferenceScreenSpaceX, positionUpperLeftReferenceScreenSpacey)
                    .size (650, 244)
                    .title (title)
                    .titleHeight (51)
                    .border (28)
                    .buttonSpacing (16)
                    .buttonWidth (120)
                    .message (message)
                    .textBoxPaddingHorizontal (2)
                    .textBoxPaddingBottom (21)
                    .textPaddingHorizontal (4)
                    .textPaddingBottom (4)
                    .build (),
            stage, listener);
    // @formatter:on

    changeButtonText ("OK", "YES");
    changeButtonText ("CANCEL", "NO");
  }

  @Override
  protected void addKeys ()
  {
    // This override without any call to super gets rid of the ESCAPE key for the "NO" choice,
    // which is inherited from OkCancelDialog. We want the user to be required to press "YES" or "NO".

    // Since we're also inadvertently overriding OkDialog, add back the ENTER key for the "YES" choice.
    addKey (Input.Keys.ENTER, DialogAction.SUBMIT_AND_HIDE);
  }
}
