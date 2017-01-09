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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs;

import com.badlogic.gdx.scenes.scene2d.Action;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.quit.PlayScreenQuitDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CompositeDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.tools.common.Arguments;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class DefaultPlayScreenDialogListener extends DialogListenerAdapter implements PlayScreenDialogListener
{
  private final CompositeDialog allDialogs;
  private final MouseInput mouseInput;
  private PlayMap playMap;

  public DefaultPlayScreenDialogListener (final CompositeDialog allDialogs,
                                          final MouseInput mouseInput,
                                          final PlayMap playMap)
  {
    Arguments.checkIsNotNull (allDialogs, "allDialogs");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (playMap, "playMap");

    this.allDialogs = allDialogs;
    this.mouseInput = mouseInput;
    this.playMap = playMap;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void onShow ()
  {
    reshowQuitDialogOnTopIfShown ();
    playMap.disable ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void onHide ()
  {
    if (allDialogs.noneAreShownOf (PlayMapBlockingDialog.class)) playMap.enable (mouseInput.position ());
  }

  @Override
  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }

  protected void reshowQuitDialogOnTopIfShown ()
  {
    final Dialog quitDialog = allDialogs.get (PlayScreenQuitDialog.class);

    if (!quitDialog.isShown ()) return;
    if (allDialogs.noneAreShownExcept (quitDialog)) return;

    // Hide and re-show to keep on top of any other dialog.
    quitDialog.hide (null);
    quitDialog.show ((Action) null);
  }
}
