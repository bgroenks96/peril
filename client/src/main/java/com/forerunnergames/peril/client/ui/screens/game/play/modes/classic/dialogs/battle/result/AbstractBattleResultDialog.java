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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkDialog;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Preconditions;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractBattleResultDialog extends OkDialog implements BattleResultDialog
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  @Nullable
  private BattleResultPacket result;

  AbstractBattleResultDialog (final WidgetFactory widgetFactory,
                              final DialogStyle style,
                              final Stage stage,
                              final DialogListener listener)
  {
    super (widgetFactory, style, stage, listener);
  }

  @Override
  public final void show (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");
    Preconditions.checkIsFalse (result.outcomeIs (BattleOutcome.CONTINUE),
                                "{} must be deterministic! (Either {} or {}, not {}).",
                                BattleOutcome.class.getSimpleName (), BattleOutcome.ATTACKER_VICTORIOUS,
                                BattleOutcome.ATTACKER_DEFEATED, BattleOutcome.CONTINUE);

    setTitle (getTitleText (result));
    setMessage (new DefaultMessage (getMessageText (result)));

    this.result = result;

    show ();
  }

  @Override
  public boolean battleOutcomeIs (final BattleOutcome outcome)
  {
    return result != null && result.outcomeIs (outcome);
  }

  @Override
  public String getAttackingCountryName ()
  {
    return result != null ? result.getAttackingCountryName () : "";
  }

  @Override
  public String getDefendingCountryName ()
  {
    return result != null ? result.getDefendingCountryName () : "";
  }

  protected abstract String getTitleText (final BattleResultPacket result);

  protected abstract String getMessageText (final BattleResultPacket result);
}
