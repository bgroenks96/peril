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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.widgets.dialogs.NullDialog;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.annotations.AllowNegative;

final class NullBattleDialog extends NullDialog implements BattleDialog
{
  @Override
  public void startBattle (final PendingBattleActorPacket attacker,
                           final PendingBattleActorPacket defender,
                           final Country attackingCountry,
                           final Country defendingCountry)
  {
    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");
  }

  @Override
  public void continueBattle (final DieRange attackerDieRange, final DieRange defenderDieRange)
  {
    Arguments.checkIsNotNull (attackerDieRange, "attackerDieRange");
    Arguments.checkIsNotNull (defenderDieRange, "defenderDieRange");
  }

  @Override
  public void showBattleResult (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");
  }

  @Override
  public void playBattleEffects (@AllowNegative final int attackingCountryDeltaArmies,
                                 @AllowNegative final int defendingCountryDeltaArmies)
  {
  }

  @Override
  public void updateCountries (final Country attackingCountry, final Country defendingCountry)
  {
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");
  }

  @Override
  public boolean isBattling ()
  {
    return false;
  }

  @Override
  public int getActiveDieCount ()
  {
    return 0;
  }

  @Override
  public int getActiveAttackerDieCount ()
  {
    return 0;
  }

  @Override
  public int getActiveDefenderDieCount ()
  {
    return 0;
  }
}
