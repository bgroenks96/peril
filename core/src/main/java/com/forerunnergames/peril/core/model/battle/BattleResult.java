package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface BattleResult
{
  BattleActor getAttacker ();

  BattleActor getDefender ();

  /**
   * @return the Id of the defending country owner, after the battle has completed; Note: this Id will always be the
   *         same as the defending player's unless the battle resulted in ownership changing to the attacker.
   */
  Id getDefendingCountryOwner ();

  ImmutableSet <DieRoll> getAttackerRollResults ();

  ImmutableSet <DieRoll> getDefenderRollResults ();
}
