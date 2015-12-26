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
