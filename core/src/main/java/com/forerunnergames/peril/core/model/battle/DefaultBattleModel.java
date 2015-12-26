package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmyChangeDeniedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
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
  private final GameRules rules;

  public DefaultBattleModel (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  public ImmutableSet <CountryPacket> getValidAttackTargetsFor (final Id sourceCountry, final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final CountryMapGraphModel countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();

    // if the country doesn't have enough armies, it has no valid targets
    final int armyCount = countryArmyModel.getArmyCountFor (sourceCountry);
    if (armyCount < rules.getMinArmiesOnCountryForAttack ()) return ImmutableSet.of ();

    final Id owner = countryOwnerModel.ownerOf (sourceCountry);
    final ImmutableSet <Id> adjacentCountries = countryMapGraphModel.getAdjacentNodes (sourceCountry);
    final ImmutableSet.Builder <CountryPacket> validAdjacentTargets = ImmutableSet.builder ();
    for (final Id countryId : adjacentCountries)
    {
      final CountryPacket country = countryMapGraphModel.countryPacketWith (countryId);
      if (countryOwnerModel.ownerOf (sourceCountry).isNot (owner)) validAdjacentTargets.add (country);
    }

    return validAdjacentTargets.build ();
  }

  @Override
  public DataResult <AttackOrder, Reason> newPlayerAttackOrder (final Id playerId,
                                                                final Id sourceCountry,
                                                                final Id targetCountry,
                                                                final int dieCount,
                                                                final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (dieCount, "dieCount");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final CountryMapGraphModel countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
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

    if (!countryMapGraphModel.areAdjacent (sourceCountry, targetCountry))
    {
      return DataResult.failureNoData (Reason.COUNTRIES_NOT_ADJACENT);
    }

    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountry);

    if (sourceCountryArmyCount < rules.getMinArmiesOnCountryForAttack ())
    {
      return DataResult.failureNoData (Reason.INSUFFICIENT_ARMY_COUNT);
    }

    if (dieCount < rules.getMinAttackerDieCount (sourceCountryArmyCount)
            || dieCount > rules.getMaxAttackerDieCount (sourceCountryArmyCount))
    {
      return DataResult.failureNoData (Reason.INVALID_DIE_COUNT);
    }

    final AttackOrder attackOrder = new AttackOrder (playerId, sourceCountry, targetCountry, dieCount);
    pendingAttackOrders.add (attackOrder);

    return DataResult.success (attackOrder);
  }

  @Override
  public BattleResult generateResultFor (final AttackOrder attackOrder,
                                         final int defenderDieCount,
                                         final PlayerModel playerModel,
                                         final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (attackOrder, "attackOrder");
    Arguments.checkIsNotNegative (defenderDieCount, "defenderDieCount");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Preconditions.checkIsTrue (pendingAttackOrders.contains (attackOrder),
                               Strings.format ("No pending attack order with Id: {}", attackOrder.getId ()));

    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();
    final CountryArmyModel countryArmyModel = playMapModel.getCountryArmyModel ();

    final Id attackerCountry = attackOrder.getSourceCountry ();
    final Id defenderCountry = attackOrder.getTargetCountry ();
    final Id defenderId = countryOwnerModel.ownerOf (attackOrder.getTargetCountry ());

    // assertion sanity checks
    assert countryArmyModel.armyCountIsAtLeast (rules.getMinArmiesOnCountryForAttack (), attackerCountry);
    assert attackOrder.getDieCount () >= defenderDieCount;

    final ImmutableList <DieFaceValue> attackerRoll = generatedSortedDieValues (attackOrder.getDieCount ());
    final ImmutableList <DieFaceValue> defenderRoll = generatedSortedDieValues (defenderDieCount);

    final ImmutableSortedMap.Builder <DieFaceValue, DieOutcome> attackerRollResults = ImmutableSortedMap
            .orderedBy (DieFaceValue.DESCENDING_ORDER);
    final ImmutableSortedMap.Builder <DieFaceValue, DieOutcome> defenderRollResults = ImmutableSortedMap
            .orderedBy (DieFaceValue.DESCENDING_ORDER);
    boolean battleFinished = false;
    for (int i = 0; i < defenderRoll.size (); i++)
    {
      final DieFaceValue attackerDieValue = attackerRoll.get (i);
      final DieFaceValue defenderDieValue = defenderRoll.get (i);
      final DieOutcome attackerOutcome = rules.determineAttackerOutcome (attackerDieValue, defenderDieValue);
      final DieOutcome defenderOutcome = rules.determineDefenderOutcome (attackerDieValue, defenderDieValue);

      // remove armies from losing battles iff the battle has not already been finished
      // i.e. if both parties have not yet depleted all available armies
      if (attackerOutcome == DieOutcome.LOSE && !battleFinished)
      {
        final Result <?> result;
        result = countryArmyModel.requestToRemoveArmiesFromCountry (attackerCountry, 1);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to remove army from attacking country [id={}] | Reason: {}",
                                        attackerCountry, result.getFailureReason ());
        }
      }

      if (defenderOutcome == DieOutcome.LOSE && !battleFinished)
      {
        final Result <CountryArmyChangeDeniedEvent.Reason> result;
        result = countryArmyModel.requestToRemoveArmiesFromCountry (defenderCountry, 1);
        if (result.failed ())
        {
          Exceptions.throwIllegalState ("Failed to remove army from defending country [id={}] | Reason: {}",
                                        attackerCountry, result.getFailureReason ());
        }
        if (countryArmyModel.armyCountIs (0, defenderCountry))
        {
          final Result <?> reassignmentResult;
          reassignmentResult = countryOwnerModel.requestToAssignCountryOwner (defenderCountry,
                                                                              attackOrder.getPlayerId ());
          if (reassignmentResult.failed ())
          {
            Exceptions.throwIllegalState ("Failed to re-assign owner of defending country | Reason: {}",
                                          reassignmentResult.getFailureReason ());
          }
        }
      }

      // set battleFinished to true if either the attacking country has too few armies to attack or the
      // defending country has too few to defend
      battleFinished = countryArmyModel.armyCountIs (rules.getMinArmiesOnCountryForAttack (), attackerCountry)
              || countryArmyModel.armyCountIs (rules.getMinArmiesOnCountry (), defenderCountry);

      // store die values and outcomes regardless of whether or not the battle has already finished
      attackerRollResults.put (attackerDieValue, attackerOutcome);
      defenderRollResults.put (defenderDieValue, defenderOutcome);
    }

    final BattleActor attacker = new DefaultBattleActor (attackOrder.getPlayerId (), attackerCountry,
            attackOrder.getDieCount ());
    final BattleActor defender = new DefaultBattleActor (defenderId, defenderCountry, defenderDieCount);
    final BattleResult result = new DefaultBattleResult (attacker, defender,
            countryOwnerModel.ownerOf (defenderCountry), attackerRollResults.build (), defenderRollResults.build ());

    battleResultArchive.add (result);

    return result;
  }

  @Override
  public Optional <BattleResult> getLastBattleResult ()
  {
    return Optional.fromNullable (battleResultArchive.peekLast ());
  }

  private ImmutableList <DieFaceValue> generatedSortedDieValues (final int dieCount)
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
