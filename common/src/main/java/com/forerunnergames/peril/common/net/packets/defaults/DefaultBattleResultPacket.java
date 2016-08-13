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

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableList;

public final class DefaultBattleResultPacket implements BattleResultPacket
{
  private final BattleOutcome outcome;
  private final FinalBattleActorPacket attacker;
  private final FinalBattleActorPacket defender;
  private final PlayerPacket defendingCountryOwner;
  private final ImmutableList <DieRoll> attackerRolls;
  private final ImmutableList <DieRoll> defenderRolls;
  private final int attackingCountryArmyDelta;
  private final int defendingCountryArmyDelta;

  public DefaultBattleResultPacket (final BattleOutcome outcome,
                                    final FinalBattleActorPacket attacker,
                                    final FinalBattleActorPacket defender,
                                    final PlayerPacket defendingCountryOwner,
                                    final ImmutableList <DieRoll> attackerRolls,
                                    final ImmutableList <DieRoll> defenderRolls,
                                    final int attackingCountryArmyDelta,
                                    final int defendingCountryArmyDelta)
  {
    Arguments.checkIsNotNull (outcome, "outcome");
    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNull (defendingCountryOwner, "defendingCountryOwner");
    Arguments.checkIsNotNull (attackerRolls, "attackerRolls");
    Arguments.checkIsNotNull (defenderRolls, "defenderRolls");

    this.outcome = outcome;
    this.attacker = attacker;
    this.defender = defender;
    this.defendingCountryOwner = defendingCountryOwner;
    this.attackerRolls = attackerRolls;
    this.defenderRolls = defenderRolls;
    this.attackingCountryArmyDelta = attackingCountryArmyDelta;
    this.defendingCountryArmyDelta = defendingCountryArmyDelta;
  }

  @Override
  public BattleOutcome getOutcome ()
  {
    return outcome;
  }

  @Override
  public boolean outcomeIs (final BattleOutcome outcome)
  {
    Arguments.checkIsNotNull (outcome, "outcome");

    return this.outcome == outcome;
  }

  @Override
  public FinalBattleActorPacket getAttacker ()
  {
    return attacker;
  }

  @Override
  public FinalBattleActorPacket getDefender ()
  {
    return defender;
  }

  @Override
  public PlayerPacket getDefendingCountryOwner ()
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
  public DieRange getAttackerDieRange ()
  {
    return attacker.getDieRange ();
  }

  @Override
  public DieRange getDefenderDieRange ()
  {
    return defender.getDieRange ();
  }

  @Override
  public int getAttackerDieCount ()
  {
    return attacker.getDieCount ();
  }

  @Override
  public int getDefenderDieCount ()
  {
    return defender.getDieCount ();
  }

  @Override
  public PlayerPacket getAttackingPlayer ()
  {
    return attacker.getPlayer ();
  }

  @Override
  public PlayerPacket getDefendingPlayer ()
  {
    return defender.getPlayer ();
  }

  @Override
  public String getAttackingPlayerName ()
  {
    return attacker.getPlayerName ();
  }

  @Override
  public String getDefendingPlayerName ()
  {
    return defender.getPlayerName ();
  }

  @Override
  public PlayerColor getAttackingPlayerColor ()
  {
    return attacker.getPlayerColor ();
  }

  @Override
  public PlayerColor getDefendingPlayerColor ()
  {
    return defender.getPlayerColor ();
  }

  @Override
  public CountryPacket getAttackingCountry ()
  {
    return attacker.getCountry ();
  }

  @Override
  public CountryPacket getDefendingCountry ()
  {
    return defender.getCountry ();
  }

  @Override
  public String getAttackingCountryName ()
  {
    return attacker.getCountryName ();
  }

  @Override
  public String getDefendingCountryName ()
  {
    return defender.getCountryName ();
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
  public int getAttackingCountryArmyCount ()
  {
    return attacker.getCountryArmyCount ();
  }

  @Override
  public int getDefendingCountryArmyCount ()
  {
    return defender.getCountryArmyCount ();
  }

  @Override
  public String toString ()
  {
    // @formatter:off
    return Strings.format ("{}: Outcome: [{}] | Attacker: [{}] | Defender: [{}] | DefendingCountryOwner: [{}] | "
            + "AttackerRolls: [{}] | DefenderRolls: [{}] | AttackingCountryArmyDelta: [{}]"
            + " | DefendingCountryArmyDelta: [{}]", getClass ().getSimpleName (), outcome, attacker, defender,
            defendingCountryOwner, attackerRolls, defenderRolls, attackingCountryArmyDelta, defendingCountryArmyDelta);
    // @formatter:on
  }

  @RequiredForNetworkSerialization
  private DefaultBattleResultPacket ()
  {
    outcome = null;
    attacker = null;
    defender = null;
    defendingCountryOwner = null;
    attackerRolls = null;
    defenderRolls = null;
    attackingCountryArmyDelta = 0;
    defendingCountryArmyDelta = 0;
  }
}
