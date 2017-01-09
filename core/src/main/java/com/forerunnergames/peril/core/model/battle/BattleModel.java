/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public interface BattleModel
{
  ImmutableSet <CountryPacket> getValidAttackTargetsFor (final Id sourceCountry, final PlayMapModel playMapModel);

  /**
   * Validates the given attack order data and returns a DataResult containing an AttackOrder on success or Reason on
   * failure.
   */
  DataResult <AttackVector, PlayerSelectAttackVectorDeniedEvent.Reason> newPlayerAttackVector (final Id playerId,
                                                                                               final Id sourceCountry,
                                                                                               final Id targetCountry);

  DataResult <AttackOrder, PlayerAttackCountryDeniedEvent.Reason> newPlayerAttackOrder (final AttackVector attackVector,
                                                                                        final int dieCount);

  BattleResult generateResultFor (final AttackOrder attackOrder,
                                  final int defenderDieCount,
                                  final PlayerModel playerModel);

  Optional <BattleResult> getLastBattleResult ();
}
