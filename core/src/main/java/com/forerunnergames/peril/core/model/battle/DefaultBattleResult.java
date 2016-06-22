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

import com.google.common.collect.ImmutableList;

public final class DefaultBattleResult implements BattleResult
{
  private final FinalBattleActor attacker;
  private final FinalBattleActor defender;
  private final Id defendingCountryOwner;
  private final ImmutableList <DieRoll> attackerRolls;
  private final ImmutableList <DieRoll> defenderRolls;

  public DefaultBattleResult (final FinalBattleActor attacker,
                              final FinalBattleActor defender,
                              final Id defendingCountryOwner,
                              final ImmutableList <DieRoll> attackerRolls,
                              final ImmutableList <DieRoll> defenderRolls)
  {
    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNull (defendingCountryOwner, "defendingCountryOwner");
    Arguments.checkIsNotNull (attackerRolls, "attackerRolls");
    Arguments.checkIsNotNull (defenderRolls, "defenderRolls");

    this.attacker = attacker;
    this.defender = defender;
    this.defendingCountryOwner = defendingCountryOwner;
    this.attackerRolls = attackerRolls;
    this.defenderRolls = defenderRolls;
  }

  @Override
  public FinalBattleActor getAttacker ()
  {
    return attacker;
  }

  @Override
  public FinalBattleActor getDefender ()
  {
    return defender;
  }

  @Override
  public Id getDefendingCountryOwner ()
  {
    return defendingCountryOwner;
  }

  @Override
  public ImmutableList <DieRoll> getAttackerRolls ()
  {
    return attackerRolls;
  }

  @Override
  public ImmutableList <DieRoll> getDefenderRolls ()
  {
    return defenderRolls;
  }

  @Override
  public String toString ()
  {
    // @formatter:off
    return Strings.format ("{}: Attacker: [{}] | Defender: [{}] | DefendingCountryOwner: [{}] | "
                                   + "AttackerRolls: [{}] | DefenderRollsults: [{}]", getClass ().getSimpleName (),
                           attacker, defender, defendingCountryOwner, attackerRolls, defenderRolls);
    // @formatter:on
  }
}
