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

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public final class DefaultBattleModel implements BattleModel
{
  private static final ImmutableList <DieFaceValue> DIE_VALUES = ImmutableList.copyOf (DieFaceValue.values ());
  private final Set <AttackOrder> pendingAttackOrders = Sets.newHashSet ();
  private final Deque <BattleResult> battleResultArchive = Queues.newArrayDeque ();
  private final PlayMapModel playMapModel;
  private final GameRules rules;

  public DefaultBattleModel (final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    this.playMapModel = playMapModel;
    rules = playMapModel.getRules ();
  }

  @Override
  public ImmutableSet <CountryPacket> getValidAttackTargetsFor (final Id sourceCountry, final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final CountryGraphModel countryGraphModel = playMapModel.getCountryGraphModel ();
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();

    // if the country doesn't have enough armies, it has no valid targets
    final int armyCount = countryArmyModel.getArmyCountFor (sourceCountry);
    if (armyCount < rules.getMinArmiesOnCountryForAttack ()) return ImmutableSet.of ();

    final Id owner = countryOwnerModel.ownerOf (sourceCountry);
    final ImmutableSet <Id> adjacentCountries = countryGraphModel.getAdjacentNodes (sourceCountry);
    final ImmutableSet.Builder <CountryPacket> validAdjacentTargets = ImmutableSet.builder ();
    for (final Id countryId : adjacentCountries)
    {
      final CountryPacket country = countryGraphModel.countryPacketWith (countryId);
      if (countryOwnerModel.ownerOf (countryId).isNot (owner)) validAdjacentTargets.add (country);
    }

    return validAdjacentTargets.build ();
  }

  @Override
  public DataResult <AttackVector, PlayerSelectAttackVectorDeniedEvent.Reason> newPlayerAttackVector (final Id playerId,
                                                                                                      final Id sourceCountry,
                                                                                                      final Id targetCountry)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");

    final CountryGraphModel countryGraphModel = playMapModel.getCountryGraphModel ();
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();

    if (!countryOwnerModel.isCountryOwnedBy (sourceCountry, playerId))
    {
      return DataResult.failureNoData (Reason.NOT_OWNER_OF_SOURCE_COUNTRY);
    }

    if (countryOwnerModel.isCountryOwnedBy (targetCountry, playerId))
    {
      return DataResult.failureNoData (Reason.ALREADY_OWNER_OF_TARGET_COUNTRY);
    }

    if (!countryGraphModel.areAdjacent (sourceCountry, targetCountry))
    {
      return DataResult.failureNoData (Reason.COUNTRIES_NOT_ADJACENT);
    }

    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountry);

    if (sourceCountryArmyCount < rules.getMinArmiesOnCountryForAttack ())
    {
      return DataResult.failureNoData (Reason.INSUFFICIENT_ARMY_COUNT);
    }

    final AttackVector attackVector = new DefaultAttackVector (playerId, sourceCountry, targetCountry);
    return DataResult.success (attackVector);
  }

  @Override
  public DataResult <AttackOrder, PlayerAttackCountryDeniedEvent.Reason> newPlayerAttackOrder (final AttackVector attackVector,
                                                                                               final int dieCount)
  {
    Arguments.checkIsNotNull (attackVector, "attackVector");
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();

    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (attackVector.getSourceCountry ());

    if (sourceCountryArmyCount < rules.getMinArmiesOnCountryForAttack ())
    {
      return DataResult.failureNoData (PlayerAttackCountryDeniedEvent.Reason.INSUFFICIENT_ARMY_COUNT);
    }

    if (dieCount < rules.getMinAttackerDieCount (sourceCountryArmyCount)
            || dieCount > rules.getMaxAttackerDieCount (sourceCountryArmyCount))
    {
      return DataResult.failureNoData (PlayerAttackCountryDeniedEvent.Reason.INVALID_DIE_COUNT);
    }

    final AttackOrder attackOrder = new DefaultAttackOrder (attackVector, dieCount);
    pendingAttackOrders.add (attackOrder);
    return DataResult.success (attackOrder);
  }

  @Override
  public BattleResult generateResultFor (final AttackOrder attackOrder,
                                         final int defenderDieCount,
                                         final PlayerModel playerModel)
  {
    Arguments.checkIsNotNull (attackOrder, "attackOrder");
    Arguments.checkIsNotNegative (defenderDieCount, "defenderDieCount");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Preconditions.checkIsTrue (pendingAttackOrders.contains (attackOrder),
                               Strings.format ("No pending attack order with Id: {}", attackOrder.getId ()));

    pendingAttackOrders.remove (attackOrder);

    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();
    final AttackVector attackVector = attackOrder.getAttackVector ();

    final Id attackerCountryId = attackVector.getSourceCountry ();
    final Id defenderCountryId = attackVector.getTargetCountry ();
    final Id defenderId = countryOwnerModel.ownerOf (attackVector.getTargetCountry ());

    // The die ranges actually used for this attack, must be obtained before armies are removed from countries.
    final DieRange attackerDieRange = rules.getAttackerDieRange (countryArmyModel.getArmyCountFor (attackerCountryId));
    final DieRange defenderDieRange = rules.getDefenderDieRange (countryArmyModel.getArmyCountFor (defenderCountryId));

    // assertion sanity checks
    assert countryArmyModel.armyCountIsAtLeast (rules.getMinArmiesOnCountryForAttack (), attackerCountryId);

    final ImmutableList <DieFaceValue> attackerRoll = generateSortedDieValues (attackOrder.getDieCount ());
    final ImmutableList <DieFaceValue> defenderRoll = generateSortedDieValues (defenderDieCount);

    final ImmutableList.Builder <DieRoll> attackerRolls = ImmutableList.builder ();
    final ImmutableList.Builder <DieRoll> defenderRolls = ImmutableList.builder ();
    final int maxDieCount = Math.max (attackerRoll.size (), defenderRoll.size ());
    BattleOutcome battleOutcome = BattleOutcome.CONTINUE;
    for (int i = 0; i < maxDieCount; i++)
    {
      // Guard:
      // We ran out of attacker dice, but there is an extra defender die.
      // Keep its value, but make it lose; the dice are sorted lowest to highest, so this is a loser / bottom feeder.
      // The battle state is unaffected by such die rolls, so we can skip the rest of the loop iteration.
      if (i >= attackerRoll.size ())
      {
        defenderRolls.add (new DieRoll (defenderRoll.get (i), DieOutcome.LOSE));
        continue;
      }

      // Guard:
      // We ran out of defender dice, but there is an extra attacker die.
      // Keep its value, but make it lose; the dice are sorted lowest to highest, so this is a loser / bottom feeder.
      // The battle state is unaffected by such die rolls, so we can skip the rest of the loop iteration.
      if (i >= defenderRoll.size ())
      {
        attackerRolls.add (new DieRoll (attackerRoll.get (i), DieOutcome.LOSE));
        continue;
      }

      // We now have an attacker & defender die to face off in battle!
      final DieFaceValue attackerDieValue = attackerRoll.get (i);
      final DieFaceValue defenderDieValue = defenderRoll.get (i);
      final DieOutcome attackerOutcome = rules.determineAttackerOutcome (attackerDieValue, defenderDieValue);
      final DieOutcome defenderOutcome = rules.determineDefenderOutcome (defenderDieValue, attackerDieValue);

      // remove armies from losing battles if the battle has not already been finished
      // i.e. if both parties have not yet depleted all available armies
      if (attackerOutcome == DieOutcome.LOSE && battleOutcome == BattleOutcome.CONTINUE)
      {
        final MutatorResult <?> result = countryArmyModel.requestToRemoveArmiesFromCountry (attackerCountryId, 1);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to remove army from attacking country [id={}] | Reason: {}",
                                        attackerCountryId, result.getFailureReason ());
        }

        result.commitIfSuccessful ();
      }

      if (defenderOutcome == DieOutcome.LOSE && battleOutcome == BattleOutcome.CONTINUE)
      {
        final MutatorResult <?> result = countryArmyModel.requestToRemoveArmiesFromCountry (defenderCountryId, 1);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to remove army from defending country [id={}] | Reason: {}",
                                        attackerCountryId, result.getFailureReason ());
        }

        result.commitIfSuccessful ();

        if (countryArmyModel.armyCountIs (0, defenderCountryId))
        {
          final MutatorResult <?> reassignmentResult;
          reassignmentResult = countryOwnerModel.requestToReassignCountryOwner (defenderCountryId,
                                                                                attackVector.getPlayerId ());
          if (reassignmentResult.failed ())
          {
            Exceptions.throwIllegalState ("Failed to re-assign owner of defending country | Reason: {}",
                                          reassignmentResult.getFailureReason ());
          }

          reassignmentResult.commitIfSuccessful ();
        }
      }

      final int attackingCountryArmyCount = countryArmyModel.getArmyCountFor (attackerCountryId);
      final int defendingCountryArmyCount = countryArmyModel.getArmyCountFor (defenderCountryId);
      battleOutcome = rules.getBattleOutcome (attackingCountryArmyCount, defendingCountryArmyCount);

      // store die values and outcomes regardless of whether or not the battle has already finished
      attackerRolls.add (new DieRoll (attackerDieValue, attackerOutcome));
      defenderRolls.add (new DieRoll (defenderDieValue, defenderOutcome));
    }

    final FinalBattleActor attacker = new DefaultFinalBattleActor (attackVector.getPlayerId (), attackerCountryId,
            attackerDieRange, attackOrder.getDieCount ());
    final FinalBattleActor defender = new DefaultFinalBattleActor (defenderId, defenderCountryId, defenderDieRange,
            defenderDieCount);
    final BattleResult result = new DefaultBattleResult (battleOutcome, attacker, defender,
            countryOwnerModel.ownerOf (defenderCountryId), attackerRolls.build (), defenderRolls.build ());

    battleResultArchive.add (result);

    return result;
  }

  @Override
  public Optional <BattleResult> getLastBattleResult ()
  {
    return Optional.fromNullable (battleResultArchive.peekLast ());
  }

  private ImmutableList <DieFaceValue> generateSortedDieValues (final int dieCount)
  {
    final List <DieFaceValue> results = Lists.newArrayList ();
    for (int i = 0; i < dieCount; i++)
    {
      final DieFaceValue randomValue = Randomness.getRandomElementFrom (DIE_VALUES);
      results.add (randomValue);
    }
    Collections.sort (results, DieFaceValue.DESCENDING_ORDER);
    return ImmutableList.copyOf (results);
  }
}
