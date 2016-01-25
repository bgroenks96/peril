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

package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class DefaultBattleResult implements BattleResult
{
  private final BattleActor attacker;
  private final BattleActor defender;
  private final Id defendingCountryOwner;
  private final ImmutableSet <DieRoll> attackerRollResults;
  private final ImmutableSet <DieRoll> defenderRollResults;

  public DefaultBattleResult (final BattleActor attacker,
                              final BattleActor defender,
                              final Id defendingCountryOwner,
                              final ImmutableSet <DieRoll> attackerRollResults,
                              final ImmutableSet <DieRoll> defenderRollResults)
  {
    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNull (defendingCountryOwner, "defendingCountryOwner");
    Arguments.checkIsNotNull (attackerRollResults, "attackerRollResults");
    Arguments.checkIsNotNull (defenderRollResults, "defenderRollResults");

    this.attacker = attacker;
    this.defender = defender;
    this.defendingCountryOwner = defendingCountryOwner;
    this.attackerRollResults = attackerRollResults;
    this.defenderRollResults = defenderRollResults;
  }

  @Override
  public BattleActor getAttacker ()
  {
    return attacker;
  }

  @Override
  public BattleActor getDefender ()
  {
    return defender;
  }

  @Override
  public Id getDefendingCountryOwner ()
  {
    return defendingCountryOwner;
  }

  @Override
  public ImmutableSet <DieRoll> getAttackerRollResults ()
  {
    return attackerRollResults;
  }

  @Override
  public ImmutableSet <DieRoll> getDefenderRollResults ()
  {
    return defenderRollResults;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Attacker: [{}] | Defender: [{}] | DefendingCountryOwner: [{}] | AttackerRollResults: [{}] | DefenderRollResults: [{}]",
                           getClass ().getSimpleName (), attacker, defender, defendingCountryOwner, attackerRollResults,
                           defenderRollResults);
  }
}
