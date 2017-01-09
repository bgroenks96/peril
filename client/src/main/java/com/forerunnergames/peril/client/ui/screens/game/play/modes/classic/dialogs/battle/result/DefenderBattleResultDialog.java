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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.tools.common.Arguments;

public final class DefenderBattleResultDialog extends AbstractBattleResultDialog
{
  public DefenderBattleResultDialog (final WidgetFactory widgetFactory,
                                     final DialogStyle style,
                                     final Stage stage,
                                     final DialogListener listener)
  {
    super (widgetFactory, style, stage, listener);
  }

  @Override
  protected String getTitleText (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    switch (result.getOutcome ())
    {
      case ATTACKER_VICTORIOUS:
      {
        return "Defeat";
      }
      case ATTACKER_DEFEATED:
      {
        return "Victory";
      }
      default:
      {
        return "";
      }
    }
  }

  @Override
  protected String getMessageText (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    switch (result.getOutcome ())
    {
      case ATTACKER_VICTORIOUS:
      {
        return "General, we have failed to hold " + result.getDefendingCountryName () + ". The enemy has taken it.";
      }
      case ATTACKER_DEFEATED:
      {
        return "Congratulations, General, you have successfully repelled the attack on "
                + result.getDefendingCountryName () + "!";
      }
      default:
      {
        return "";
      }
    }
  }
}
