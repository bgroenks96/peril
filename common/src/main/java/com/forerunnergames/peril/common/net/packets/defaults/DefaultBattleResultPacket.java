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

  public DefaultBattleResultPacket (final BattleActorPacket attacker,
                                    final BattleActorPacket defender,
                                    final PlayerPacket defendingCountryOwner,
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
  public String toString ()
  {
    return Strings.format (
                           "{}: Attacker: [{}] | Defender: [{}] | DefendingCountryOwner: [{}] | AttackerRollResults: [{}] | DefenderRollResults: [{}]",
                           getClass ().getSimpleName (), attacker, defender, defendingCountryOwner, attackerRollResults,
                           defenderRollResults);
  }

  @RequiredForNetworkSerialization
  private DefaultBattleResultPacket ()
  {
    attacker = null;
    defender = null;
    defendingCountryOwner = null;
    attackerRollResults = null;
    defenderRollResults = null;
  }
}
