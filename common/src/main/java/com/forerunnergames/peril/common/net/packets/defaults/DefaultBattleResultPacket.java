package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSortedMap;

public final class DefaultBattleResultPacket implements BattleResultPacket
{
  private final BattleActorPacket attacker;
  private final BattleActorPacket defender;
  private final PlayerPacket defendingCountryOwner;
  private final ImmutableSortedMap <Integer, DieOutcome> attackerRollResults;
  private final ImmutableSortedMap <Integer, DieOutcome> defenderRollResults;

  public DefaultBattleResultPacket (final BattleActorPacket attacker,
                                    final BattleActorPacket defender,
                                    final PlayerPacket defendingCountryOwner,
                                    final ImmutableSortedMap <Integer, DieOutcome> attackerRollResults,
                                    final ImmutableSortedMap <Integer, DieOutcome> defenderRollResults)
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
  public ImmutableSortedMap <Integer, DieOutcome> getAttackerRollResults ()
  {
    return attackerRollResults;
  }

  @Override
  public ImmutableSortedMap <Integer, DieOutcome> getDefenderRollResults ()
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
