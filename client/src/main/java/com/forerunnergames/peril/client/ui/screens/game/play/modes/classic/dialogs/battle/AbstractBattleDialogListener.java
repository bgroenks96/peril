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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.DefaultPlayScreenDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CompositeDialog;
import com.forerunnergames.tools.common.Strings;

public abstract class AbstractBattleDialogListener extends DefaultPlayScreenDialogListener
        implements BattleDialogListener
{
  public AbstractBattleDialogListener (final CompositeDialog allDialogs,
                                       final PlayMap playMap,
                                       final MouseInput mouseInput)
  {
    super (allDialogs, mouseInput, playMap);
  }

  @Override
  public final void onSubmit ()
  {
    throw new UnsupportedOperationException (
            Strings.format ("The behavior of this method is intentionally undefined for {}.",
                            BattleDialog.class.getSimpleName ()));
  }
}
