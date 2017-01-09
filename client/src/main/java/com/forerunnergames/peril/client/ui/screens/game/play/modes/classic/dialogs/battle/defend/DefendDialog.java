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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.AbstractBattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialogWidgetFactory;

public final class DefendDialog extends AbstractBattleDialog
{
  private static final String TITLE_TEXT = "DEFEND";

  public DefendDialog (final BattleDialogWidgetFactory widgetFactory,
                       final Stage stage,
                       final ScreenShaker screenShaker,
                       final DefendDialogListener listener)
  {
    super (widgetFactory, TITLE_TEXT, stage, screenShaker, listener);
  }

  @Override
  protected void addButtons ()
  {
  }

  @Override
  protected void addKeys ()
  {
  }

  @Override
  protected void setDiceTouchable (final boolean areTouchable)
  {
    setAttackerDiceTouchable (false);
    setDefenderDiceTouchable (areTouchable);
  }

  @Override
  public int getActiveDieCount ()
  {
    return getActiveDefenderDieCount ();
  }
}
