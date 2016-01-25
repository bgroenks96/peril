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

import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
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
  DataResult <AttackOrder, Reason> newPlayerAttackOrder (final Id playerId,
                                                         final Id sourceCountry,
                                                         final Id targetCountry,
                                                         final int dieCount,
                                                         final PlayMapModel playMapModel);

  BattleResult generateResultFor (final AttackOrder attackOrder,
                                  final int defenderDieCount,
                                  final PlayerModel playerModel,
                                  final PlayMapModel playMapModel);

  Optional <BattleResult> getLastBattleResult ();
}
