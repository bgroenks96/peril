package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderAttackDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginAttackWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerDefendCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueAttackOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerOccupyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.EndPlayerTurnAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectAttackVectorSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.core.model.battle.AttackOrder;
import com.forerunnergames.peril.core.model.battle.AttackVector;
import com.forerunnergames.peril.core.model.battle.BattlePackets;
import com.forerunnergames.peril.core.model.battle.BattleResult;
import com.forerunnergames.peril.core.model.battle.DefaultFinalBattleActor;
import com.forerunnergames.peril.core.model.battle.DefaultPendingBattleActor;
import com.forerunnergames.peril.core.model.battle.FinalBattleActor;
import com.forerunnergames.peril.core.model.battle.PendingBattleActor;
import com.forerunnergames.peril.core.model.game.CacheKey;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandler;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTimerDuration;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.peril.core.model.state.events.BattleResultContinueEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultDefeatEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultVictoryEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

public final class DefaultAttackPhaseHandler extends AbstractGamePhaseHandler implements AttackPhaseHandler
{
  private static final long BATTLE_RESPONSE_TIMEOUT_MS = (long) GameSettings.BATTLE_RESPONSE_TIMEOUT_SECONDS * 1000;

  public DefaultAttackPhaseHandler (final GameModelConfiguration gameModelConfig)
  {
    super (gameModelConfig);
  }

  @StateEntryAction
  @Override
  protected void onBegin ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    log.info ("Begin attack phase for player [{}].", currentPlayer);
    publish (new BeginAttackPhaseEvent (currentPlayer));
  }

  @StateExitAction
  @Override
  protected void onEnd ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();
    log.info ("End attack phase for player [{}].", player);
    publish (new EndAttackPhaseEvent (player));
  }

  @Override
  @StateEntryAction
  public void waitForPlayerToSelectAttackVector ()
  {
    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> builder = ImmutableMultimap.builder ();
    for (final CountryPacket country : countryOwnerModel.getCountryPacketsOwnedBy (getCurrentPlayerId ()))
    {
      final Id countryId = countryGraphModel.countryWith (country.getName ());
      builder.putAll (country, battleModel.getValidAttackTargetsFor (countryId, playMapModel));
    }

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    publish (new PlayerBeginAttackEvent (playerPacket, builder.build ()));
    publish (new PlayerBeginAttackWaitEvent (playerPacket));
    publish (new EndPlayerTurnAvailableEvent (playerPacket));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerAttackVector (final PlayerSelectAttackVectorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id currentPlayer = getCurrentPlayerId ();
    final PlayerPacket currentPlayerPacket = getCurrentPlayerPacket ();

    final String sourceCountryName = event.getSourceCountryName ();
    final Id sourceCountry = countryGraphModel.countryWith (sourceCountryName);
    final String targetCountryName = event.getTargetCounryName ();
    final Id targetCountry = countryGraphModel.countryWith (targetCountryName);

    if (!countryGraphModel.existsCountryWith (sourceCountryName))
    {
      publish (new PlayerSelectAttackVectorDeniedEvent (currentPlayerPacket,
              PlayerSelectAttackVectorDeniedEvent.Reason.SOURCE_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    if (!countryGraphModel.existsCountryWith (targetCountryName))
    {
      publish (new PlayerSelectAttackVectorDeniedEvent (currentPlayerPacket,
              PlayerSelectAttackVectorDeniedEvent.Reason.TARGET_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    final DataResult <AttackVector, PlayerSelectAttackVectorDeniedEvent.Reason> result;
    result = battleModel.newPlayerAttackVector (currentPlayer, sourceCountry, targetCountry);
    if (result.failed ())
    {
      publish (new PlayerSelectAttackVectorDeniedEvent (currentPlayerPacket, result.getFailureReason ()));
      return false;
    }

    final AttackVector vector = result.getReturnValue ();
    publish (new PlayerSelectAttackVectorSuccessEvent (currentPlayerPacket, createPendingAttackerPacket (vector),
            createPendingDefenderPacket (vector)));

    turnDataCache.put (CacheKey.BATTLE_ATTACK_VECTOR, result.getReturnValue ());

    return true;
  }

  @Override
  @StateTransitionAction
  public void waitForPlayerAttackOrder ()
  {
    checkCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);

    final AttackVector vector = turnDataCache.get (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    final PendingBattleActorPacket attacker = createPendingAttackerPacket (vector);
    final PendingBattleActorPacket defender = createPendingDefenderPacket (vector);

    publish (new PlayerIssueAttackOrderEvent (attacker, defender));
    publish (new PlayerIssueAttackOrderWaitEvent (attacker.getPlayer (), attacker, defender));

    publish (new PlayerDefendCountryRequestEvent (attacker, defender));
    publish (new PlayerDefendCountryWaitEvent (defender.getPlayer (), attacker, defender));
    publish (new EndPlayerTurnAvailableEvent (getCurrentPlayerPacket ()));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerAttackOrder (final PlayerOrderAttackRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayerPacket = getCurrentPlayerPacket ();

    final Optional <AttackVector> maybe = turnDataCache.checkAndGet (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    if (!maybe.isPresent ())
    {
      // silently fail to prevent multiple requests from throwing an exception
      log.warn ("Received attack order event [{}] but no data present in cache.", event);
      return false;
    }

    final AttackVector attackVector = maybe.get ();
    final int attackerDieCount = event.getDieCount ();

    final DataResult <AttackOrder, PlayerOrderAttackDeniedEvent.Reason> result;
    result = battleModel.newPlayerAttackOrder (attackVector, attackerDieCount);

    if (result.failed ())
    {
      publish (new PlayerOrderAttackDeniedEvent (currentPlayerPacket, result.getFailureReason ()));
      return false;
    }

    final AttackOrder order = result.getReturnValue ();
    final FinalBattleActor attacker = createFinalAttacker (attackVector, order.getDieCount ());

    turnDataCache.put (CacheKey.BATTLE_ATTACK_ORDER, result.getReturnValue ());
    turnDataCache.put (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER, attacker);

    return turnDataCache.isSet (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER);
  }

  @Override
  @StateTimerDuration
  public long getBattleResponseTimeoutMs ()
  {
    return BATTLE_RESPONSE_TIMEOUT_MS;
  }

  @Override
  @StateTransitionCondition
  public boolean handleAttackerTimeout ()
  {
    checkCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);

    final AttackVector attackVector = turnDataCache.get (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    final CountryPacket attackingCountry = countryGraphModel.countryPacketWith (attackVector.getSourceCountry ());

    // Use the maximum dice since the player did not choose a die count in time.
    final int dieCount = rules.getMaxAttackerDieCount (attackingCountry.getArmyCount ());

    turnDataCache.put (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER, createFinalAttacker (attackVector, dieCount));

    // TODO: Core needs some mechanism of telling server to clear stale response-request cache entries
    // fix for this would be related to PERIL-372

    return turnDataCache.isSet (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER);
  }

  @Override
  @StateTransitionCondition
  public boolean handleDefenderTimeout ()
  {
    checkCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);

    final AttackVector attackVector = turnDataCache.get (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    final CountryPacket defendingCountry = countryGraphModel.countryPacketWith (attackVector.getTargetCountry ());

    // Use the maximum dice since the player did not choose a die count in time.
    final int dieCount = rules.getMaxDefenderDieCount (defendingCountry.getArmyCount ());

    turnDataCache.put (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER, createFinalDefender (attackVector, dieCount));

    // TODO: Core needs some mechanism of telling server to clear stale response-request cache entries
    // fix for this would be related to PERIL-372

    return turnDataCache.isSet (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER);
  }

  @Override
  @StateTransitionAction
  public void processPlayerRetreat (final PlayerOrderRetreatRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    checkCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);

    // @formatter:off
    final AttackVector attackVector = turnDataCache.get (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    final PlayerPacket attackingPlayer = playerModel.playerPacketWith (attackVector.getPlayerId ());
    final PlayerPacket defendingPlayer = playerModel.playerPacketWith (countryOwnerModel.ownerOf (attackVector.getTargetCountry ()));
    final CountryPacket attackingCountry = countryGraphModel.countryPacketWith (attackVector.getSourceCountry ());
    final CountryPacket defendingCountry = countryGraphModel.countryPacketWith (attackVector.getTargetCountry ());
    // @formatter:on

    publish (new PlayerOrderRetreatSuccessEvent (attackingPlayer, defendingPlayer, attackingCountry, defendingCountry));

    // Handle the corner case where PlayerDefendCountryResponseRequest was received BEFORE
    // PlayerOrderRetreatRequestEvent. PlayerDefendCountryResponseRequestEvent should always be answered, and the only
    // appropriate response here is a denial.
    //
    // Note: There is another corner case where PlayerDefendCountryResponseRequest could be received AFTER
    // PlayerOrderRetreatRequestEvent, but there is no sane way to deal with it because we will already be waiting for
    // the attacking player to select a new attack vector.
    if (turnDataCache.isSet (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER))
    {
      publish (new PlayerDefendCountryResponseDeniedEvent (defendingPlayer,
              PlayerDefendCountryResponseDeniedEvent.Reason.ATTACKER_RETREATED));

      clearCacheValues (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER);
    }

    clearCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);
  }

  @Override
  @StateTransitionAction
  public void processPlayerEndAttackPhase (final PlayerEndAttackPhaseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    publish (new PlayerEndAttackPhaseSuccessEvent (currentPlayer));

    clearCacheValues (CacheKey.BATTLE_ATTACK_VECTOR, CacheKey.BATTLE_ATTACK_ORDER, CacheKey.FINAL_BATTLE_ACTOR_ATTACKER,
                      CacheKey.FINAL_BATTLE_ACTOR_DEFENDER, CacheKey.OCCUPY_SOURCE_COUNTRY,
                      CacheKey.OCCUPY_TARGET_COUNTRY, CacheKey.OCCUPY_PREV_OWNER, CacheKey.OCCUPY_NEW_OWNER,
                      CacheKey.OCCUPY_MIN_ARMY_COUNT, CacheKey.OCCUPY_MAX_ARMY_COUNT);
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerDefendCountryResponseRequest (final PlayerDefendCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Optional <PlayerPacket> sender = internalCommHandler.senderOf (event);
    if (!sender.isPresent ())
    {
      log.warn ("No registered sender for event [{}].", event);
      return false;
    }

    checkCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);

    final AttackVector attackVector = turnDataCache.get (CacheKey.BATTLE_ATTACK_VECTOR, AttackVector.class);
    final Id defendingPlayerId = countryOwnerModel.ownerOf (attackVector.getTargetCountry ());
    final PlayerPacket defendingPlayer = playerModel.playerPacketWith (defendingPlayerId);
    final CountryPacket defendingCountry = countryGraphModel.countryPacketWith (attackVector.getTargetCountry ());

    if (!defendingPlayer.equals (sender.get ()))
    {
      log.warn ("Sender of event [{}] does not match registered defending player [{}].", sender.get (),
                defendingPlayer);
      return false;
    }

    final int dieCount = event.getDieCount ();
    if (dieCount < rules.getMinDefenderDieCount (defendingCountry.getArmyCount ())
            || dieCount > rules.getMaxDefenderDieCount (defendingCountry.getArmyCount ()))
    {
      publish (new PlayerDefendCountryResponseDeniedEvent (sender.get (),
              PlayerDefendCountryResponseDeniedEvent.Reason.INVALID_DIE_COUNT));
      republishRequestFor (event);
      return false;
    }

    turnDataCache.put (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER, createFinalDefender (attackVector, dieCount));

    return turnDataCache.isSet (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER);
  }

  @Override
  @StateEntryAction
  public void processBattle ()
  {
    checkCacheValues (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER, CacheKey.FINAL_BATTLE_ACTOR_DEFENDER,
                      CacheKey.BATTLE_ATTACK_ORDER);

    final FinalBattleActor attacker = turnDataCache.get (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER, FinalBattleActor.class);
    final FinalBattleActor defender = turnDataCache.get (CacheKey.FINAL_BATTLE_ACTOR_DEFENDER, FinalBattleActor.class);

    log.info ("Processing battle: Attacker: [{}] | Defender: [{}]", asPacket (attacker), asPacket (defender));

    final Id attackingCountryId = attacker.getCountryId ();
    final Id defendingCountryId = defender.getCountryId ();
    final int initialAttackerArmyCount = countryArmyModel.getArmyCountFor (attackingCountryId);
    final int initialDefenderAmryCount = countryArmyModel.getArmyCountFor (defendingCountryId);
    final AttackOrder attackOrder = turnDataCache.get (CacheKey.BATTLE_ATTACK_ORDER, AttackOrder.class);
    final BattleResult result = battleModel.generateResultFor (attackOrder, defender.getDieCount (), playerModel);

    log.trace ("Battle result: {}", result);

    final int newAttackerArmyCount = countryArmyModel.getArmyCountFor (attackingCountryId);
    final int newDefenderArmyCount = countryArmyModel.getArmyCountFor (defendingCountryId);
    final int attackerArmyCountDelta = newAttackerArmyCount - initialAttackerArmyCount;
    final int defenderArmyCountDelta = newDefenderArmyCount - initialDefenderAmryCount;
    final CountryPacket attackerCountry = countryGraphModel.countryPacketWith (attackingCountryId);
    final CountryPacket defenderCountry = countryGraphModel.countryPacketWith (defendingCountryId);
    final Id newOwnerId = countryOwnerModel.ownerOf (attackingCountryId);
    final Id prevOwnerId = countryOwnerModel.ownerOf (defendingCountryId);
    final PlayerPacket prevOwner = playerModel.playerPacketWith (prevOwnerId);
    final PlayerPacket newOwner = playerModel.playerPacketWith (newOwnerId);

    if (attackerArmyCountDelta != 0)
    {
      publish (new DefaultCountryArmiesChangedEvent (attackerCountry, attackerArmyCountDelta));
    }
    if (defenderArmyCountDelta != 0)
    {
      publish (new DefaultCountryArmiesChangedEvent (defenderCountry, defenderArmyCountDelta));
    }

    clearCacheValues (CacheKey.FINAL_BATTLE_ACTOR_ATTACKER, CacheKey.FINAL_BATTLE_ACTOR_DEFENDER,
                      CacheKey.BATTLE_ATTACK_ORDER);

    turnDataCache.put (CacheKey.OCCUPY_PREV_OWNER, prevOwner);
    turnDataCache.put (CacheKey.OCCUPY_NEW_OWNER, newOwner);
    turnDataCache.put (CacheKey.OCCUPY_SOURCE_COUNTRY, attackerCountry);
    turnDataCache.put (CacheKey.OCCUPY_TARGET_COUNTRY, defenderCountry);
    turnDataCache.put (CacheKey.OCCUPY_MIN_ARMY_COUNT, rules.getMinOccupyArmyCount (attackOrder.getDieCount ()));
    turnDataCache.put (CacheKey.OCCUPY_MAX_ARMY_COUNT, rules.getMaxOccupyArmyCount (newAttackerArmyCount));

    final BattleResultPacket resultPacket = BattlePackets.from (result, playerModel, countryGraphModel,
                                                                attackerArmyCountDelta, defenderArmyCountDelta);

    publish (new PlayerOrderAttackSuccessEvent (resultPacket.getAttackingPlayer (), resultPacket));
    publish (new PlayerDefendCountryResponseSuccessEvent (resultPacket.getDefendingPlayer (), resultPacket));

    final BattleOutcome outcome = result.getOutcome ();
    switch (outcome)
    {
      case CONTINUE:
      {
        publish (new BattleResultContinueEvent ());
        return;
      }
      case ATTACKER_DEFEATED:
      {
        publish (new BattleResultDefeatEvent ());
        break;
      }
      case ATTACKER_VICTORIOUS:
      {
        publish (new BattleResultVictoryEvent ());
        break;
      }
      default:
      {
        Exceptions.throwIllegalState ("Unrecognized {}: [{}].", BattleOutcome.class.getSimpleName (), outcome);
      }
    }

    clearCacheValues (CacheKey.BATTLE_ATTACK_VECTOR);
  }

  @Override
  @StateEntryAction
  public void waitForPlayerToOccupyCountry ()
  {
    checkCacheValues (CacheKey.OCCUPY_SOURCE_COUNTRY, CacheKey.OCCUPY_TARGET_COUNTRY, CacheKey.OCCUPY_NEW_OWNER,
                      CacheKey.OCCUPY_MIN_ARMY_COUNT, CacheKey.OCCUPY_MAX_ARMY_COUNT);

    final CountryPacket sourceCountry = turnDataCache.get (CacheKey.OCCUPY_SOURCE_COUNTRY, CountryPacket.class);
    final CountryPacket targetCountry = turnDataCache.get (CacheKey.OCCUPY_TARGET_COUNTRY, CountryPacket.class);
    final int minOccupationArmyCount = turnDataCache.get (CacheKey.OCCUPY_MIN_ARMY_COUNT, Integer.class);
    final int maxOccupationArmyCount = turnDataCache.get (CacheKey.OCCUPY_MAX_ARMY_COUNT, Integer.class);
    final PlayerPacket newOwner = turnDataCache.get (CacheKey.OCCUPY_NEW_OWNER, PlayerPacket.class);

    publish (new PlayerOccupyCountryRequestEvent (newOwner, sourceCountry, targetCountry, minOccupationArmyCount,
            maxOccupationArmyCount));
    publish (new PlayerOccupyCountryWaitEvent (newOwner, sourceCountry, targetCountry, minOccupationArmyCount,
            maxOccupationArmyCount));
  }

  @Override
  @StateTransitionCondition
  public boolean verifyPlayerOccupyCountryResponseRequest (final PlayerOccupyCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    checkCacheValues (CacheKey.OCCUPY_SOURCE_COUNTRY, CacheKey.OCCUPY_TARGET_COUNTRY, CacheKey.OCCUPY_PREV_OWNER,
                      CacheKey.OCCUPY_MIN_ARMY_COUNT);

    final PlayerPacket player = getCurrentPlayerPacket ();

    final CountryPacket sourceCountry = turnDataCache.get (CacheKey.OCCUPY_SOURCE_COUNTRY, CountryPacket.class);
    final CountryPacket targetCountry = turnDataCache.get (CacheKey.OCCUPY_TARGET_COUNTRY, CountryPacket.class);
    final PlayerPacket prevTargetCountryOwner = turnDataCache.get (CacheKey.OCCUPY_PREV_OWNER, PlayerPacket.class);
    final Id prevTargetCountryOwnerId = playerModel.idOf (prevTargetCountryOwner.getName ());
    final int minDeltaArmyCount = turnDataCache.get (CacheKey.OCCUPY_MIN_ARMY_COUNT, Integer.class);
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (deltaArmyCount < minDeltaArmyCount)
    {
      publish (new PlayerOccupyCountryResponseDeniedEvent (player,
              PlayerOccupyCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_UNDERFLOW,
              getOriginalRequestFor (event, PlayerOccupyCountryRequestEvent.class), event));
      republishRequestFor (event);
      return false;
    }

    if (deltaArmyCount > rules.getMaxOccupyArmyCount (sourceCountry.getArmyCount ()))
    {
      publish (new PlayerOccupyCountryResponseDeniedEvent (player,
              PlayerOccupyCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_OVERFLOW,
              getOriginalRequestFor (event, PlayerOccupyCountryRequestEvent.class), event));
      republishRequestFor (event);
      return false;
    }

    final Id sourceCountryId = countryGraphModel.countryWith (sourceCountry.getName ());
    final Id targetCountryId = countryGraphModel.countryWith (targetCountry.getName ());

    final MutatorResult <PlayerOccupyCountryResponseDeniedEvent.Reason> res1, res2;
    res1 = countryArmyModel.requestToRemoveArmiesFromCountry (sourceCountryId, deltaArmyCount);
    res2 = countryArmyModel.requestToAddArmiesToCountry (targetCountryId, deltaArmyCount);
    final Optional <MutatorResult <PlayerOccupyCountryResponseDeniedEvent.Reason>> failure;
    failure = Result.firstFailedFrom (ImmutableSet.of (res1, res2));
    if (failure.isPresent ())
    {
      publish (new PlayerOccupyCountryResponseDeniedEvent (player, failure.get ().getFailureReason (),
              getOriginalRequestFor (event, PlayerOccupyCountryRequestEvent.class), event));
      republishRequestFor (event);
      return false;
    }

    MutatorResult.commitAllSuccessful (res1, res2);

    final PlayerPacket updatedPlayerPacket = getCurrentPlayerPacket ();
    final PlayerPacket updatedPrevTargetCountryOwner = playerModel.playerPacketWith (prevTargetCountryOwnerId);
    final CountryPacket updatedSourceCountry = countryGraphModel.countryPacketWith (sourceCountryId);
    final CountryPacket updatedTargetCountry = countryGraphModel.countryPacketWith (targetCountryId);
    publish (new DefaultCountryArmiesChangedEvent (updatedSourceCountry, -deltaArmyCount));
    publish (new DefaultCountryArmiesChangedEvent (updatedTargetCountry, deltaArmyCount));
    publish (new PlayerOccupyCountryResponseSuccessEvent (updatedPlayerPacket, updatedPrevTargetCountryOwner,
            updatedSourceCountry, updatedTargetCountry, deltaArmyCount));

    if (turnDataCache.isNotSet (CacheKey.PLAYER_OCCUPIED_COUNTRY))
    {
      turnDataCache.put (CacheKey.PLAYER_OCCUPIED_COUNTRY, true);
    }

    clearCacheValues (CacheKey.OCCUPY_SOURCE_COUNTRY, CacheKey.OCCUPY_TARGET_COUNTRY, CacheKey.OCCUPY_PREV_OWNER,
                      CacheKey.OCCUPY_NEW_OWNER, CacheKey.OCCUPY_MIN_ARMY_COUNT, CacheKey.OCCUPY_MAX_ARMY_COUNT);

    return true;
  }

  private PendingBattleActorPacket createPendingAttackerPacket (final AttackVector attackVector)
  {
    return asPacket (createPendingAttacker (attackVector));
  }

  private PendingBattleActorPacket createPendingDefenderPacket (final AttackVector attackVector)
  {
    return asPacket (createPendingDefender (attackVector));
  }

  private PendingBattleActor createPendingAttacker (final AttackVector attackVector)
  {
    final Id attackerCountry = attackVector.getSourceCountry ();
    final Id attackingPlayer = attackVector.getPlayerId ();
    final DieRange attackerDieRange = rules.getAttackerDieRange (countryArmyModel.getArmyCountFor (attackerCountry));
    return new DefaultPendingBattleActor (attackingPlayer, attackerCountry, attackerDieRange);
  }

  private PendingBattleActor createPendingDefender (final AttackVector attackVector)
  {
    final Id defenderCountry = attackVector.getTargetCountry ();
    final Id defendingPlayer = countryOwnerModel.ownerOf (defenderCountry);
    final DieRange defenderDieRange = rules.getDefenderDieRange (countryArmyModel.getArmyCountFor (defenderCountry));
    return new DefaultPendingBattleActor (defendingPlayer, defenderCountry, defenderDieRange);
  }

  private FinalBattleActor createFinalAttacker (final AttackVector attackVector, final int dieCount)
  {
    final Id attackerCountry = attackVector.getSourceCountry ();
    final Id attackingPlayer = attackVector.getPlayerId ();
    final DieRange attackerDieRange = rules.getAttackerDieRange (countryArmyModel.getArmyCountFor (attackerCountry));
    return new DefaultFinalBattleActor (attackingPlayer, attackerCountry, attackerDieRange, dieCount);
  }

  private FinalBattleActor createFinalDefender (final AttackVector attackVector, final int dieCount)
  {
    final Id defenderCountry = attackVector.getTargetCountry ();
    final Id defendingPlayer = countryOwnerModel.ownerOf (defenderCountry);
    final DieRange defenderDieRange = rules.getDefenderDieRange (countryArmyModel.getArmyCountFor (defenderCountry));
    return new DefaultFinalBattleActor (defendingPlayer, defenderCountry, defenderDieRange, dieCount);
  }

  private PendingBattleActorPacket asPacket (final PendingBattleActor battleActor)
  {
    return BattlePackets.from (battleActor, playerModel, countryGraphModel);
  }

  private FinalBattleActorPacket asPacket (final FinalBattleActor battleActor)
  {
    return BattlePackets.from (battleActor, playerModel, countryGraphModel);
  }
}
