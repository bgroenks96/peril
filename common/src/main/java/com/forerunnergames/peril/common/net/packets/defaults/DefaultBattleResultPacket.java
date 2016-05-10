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

package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DefaultBattleResultPacket implements BattleResultPacket
{
  private final BattleActorPacket attacker;
  private final BattleActorPacket defender;
  private final PlayerPacket defendingCountryOwner;
  private final ImmutableSet <DieRoll> attackerRollResults;
  private final ImmutableSet <DieRoll> defenderRollResults;
  private final int attackingCountryArmyDelta;
  private final int defendingCountryArmyDelta;

  public DefaultBattleResultPacket (final BattleActorPacket attacker,
                                    final BattleActorPacket defender,
                                    final PlayerPacket defendingCountryOwner,
                                    final ImmutableSet <DieRoll> attackerRollResults,
                                    final ImmutableSet <DieRoll> defenderRollResults,
                                    final int attackingCountryArmyDelta,
                                    final int defendingCountryArmyDelta)
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
    this.attackingCountryArmyDelta = attackingCountryArmyDelta;
    this.defendingCountryArmyDelta = defendingCountryArmyDelta;
  }

  @Override
  public BattleActorPacket getAttacker ()
  {
    return attacker;
  }

  @Override
  public BattleActorPacket getDefender ()
  {
    return defender;
  }

  @Override
  public PlayerPacket getDefendingCountryOwner ()
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
  public int getAttackingCountryArmyDelta ()
  {
    return attackingCountryArmyDelta;
  }

  @Override
  public int getDefendingCountryArmyDelta ()
  {
    return defendingCountryArmyDelta;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Attacker: [{}] | Defender: [{}] | DefendingCountryOwner: [{}] | "
                                   + "AttackerRollResults: [{}] | DefenderRollResults: [{}] | "
                                   + "AttackingCountryArmyDelta: [{}] | DefendingCountryArmyDelta: [{}]", getClass ()
                                   .getSimpleName (),
                           attacker, defender, defendingCountryOwner, attackerRollResults, defenderRollResults,
                           attackingCountryArmyDelta, defendingCountryArmyDelta);
  }

  @RequiredForNetworkSerialization
  private DefaultBattleResultPacket ()
  {
    attacker = null;
    defender = null;
    defendingCountryOwner = null;
    attackerRollResults = null;
    defenderRollResults = null;
    attackingCountryArmyDelta = 0;
    defendingCountryArmyDelta = 0;
  }
}
