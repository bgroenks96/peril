package com.forerunnergames.peril.common.net.packets.battle;

import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

import com.google.common.collect.ImmutableSet;

public interface BattleResultPacket
{
  BattleActorPacket getAttacker ();

  BattleActorPacket getDefender ();

  /**
   * @return the PlayerPacket representing the defending country owner, after the battle has completed; Note: the player
   *         will always be the same as the defending player unless the battle resulted in ownership changing to the
   *         attacker.
   */
  PlayerPacket getDefendingCountryOwner ();

  ImmutableSet <DieRoll> getAttackerRollResults ();

  ImmutableSet <DieRoll> getDefenderRollResults ();
}
