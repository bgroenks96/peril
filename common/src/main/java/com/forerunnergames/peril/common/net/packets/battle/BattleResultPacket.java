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

package com.forerunnergames.peril.common.net.packets.battle;

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;

import com.google.common.collect.ImmutableList;

public interface BattleResultPacket
{
  BattleOutcome getOutcome ();

  boolean outcomeIs (final BattleOutcome outcome);

  FinalBattleActorPacket getAttacker ();

  FinalBattleActorPacket getDefender ();

  /**
   * @return the PlayerPacket representing the defending country owner, after the battle has completed; Note: the player
   *         will always be the same as the defending player unless the battle resulted in ownership changing to the
   *         attacker.
   */
  PlayerPacket getDefendingCountryOwner ();

  ImmutableList <DieRoll> getAttackerRolls ();

  ImmutableList <DieRoll> getDefenderRolls ();

  DieRange getAttackerDieRange ();

  DieRange getDefenderDieRange ();

  int getAttackerDieCount ();

  int getDefenderDieCount ();

  PlayerPacket getAttackingPlayer ();

  PlayerPacket getDefendingPlayer ();

  String getAttackingPlayerName ();

  String getDefendingPlayerName ();

  PlayerColor getAttackingPlayerColor ();

  PlayerColor getDefendingPlayerColor ();

  int getAttackingPlayerCardsInHand ();

  int getDefendingPlayerCardsInHand ();

  CountryPacket getAttackingCountry ();

  CountryPacket getDefendingCountry ();

  String getAttackingCountryName ();

  String getDefendingCountryName ();

  int getAttackingCountryArmyDelta ();

  int getDefendingCountryArmyDelta ();

  int getAttackingCountryArmyCount ();

  int getDefendingCountryArmyCount ();
}
