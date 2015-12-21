package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSortedMap;

public final class DefaultBattleResult implements BattleResult
{
  private final BattleActor attacker;
  private final BattleActor defender;
  private final Id defendingCountryOwner;
  private final ImmutableSortedMap <DieFaceValue, DieOutcome> attackerRollResults;
  private final ImmutableSortedMap <DieFaceValue, DieOutcome> defenderRollResults;

  public DefaultBattleResult (final BattleActor attacker,
                              final BattleActor defender,
                              final Id defendingCountryOwner,
                              final ImmutableSortedMap <DieFaceValue, DieOutcome> attackerRollResults,
                              final ImmutableSortedMap <DieFaceValue, DieOutcome> defenderRollResults)
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
  public ImmutableSortedMap <DieFaceValue, DieOutcome> getAttackerRollResults ()
  {
    return attackerRollResults;
  }

  @Override
  public ImmutableSortedMap <DieFaceValue, DieOutcome> getDefenderRollResults ()
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
