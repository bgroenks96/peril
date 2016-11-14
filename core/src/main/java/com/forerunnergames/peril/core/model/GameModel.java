/*
 * Copyright �� 2011 - 2013 Aaron Mahan.
 * Copyright �� 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerCancelFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerEndAttackPhaseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderAttackRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderRetreatRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectAttackVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerChangeCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.EndPlayerTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerCancelFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderAttackDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.WaitingForPlayersToJoinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginAttackWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginFortificationWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerClaimCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerDefendCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueAttackOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueFortifyOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerOccupyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginAttackEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginFortificationEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueAttackOrderEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerIssueFortifyOrderEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.EndPlayerTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerCancelFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectAttackVectorSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectFortifyVectorSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.FinalBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.peril.core.events.DefaultEventFactory;
import com.forerunnergames.peril.core.events.EventFactory;
import com.forerunnergames.peril.core.events.internal.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.core.model.battle.AttackOrder;
import com.forerunnergames.peril.core.model.battle.AttackVector;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.BattlePackets;
import com.forerunnergames.peril.core.model.battle.BattleResult;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultFinalBattleActor;
import com.forerunnergames.peril.core.model.battle.DefaultPendingBattleActor;
import com.forerunnergames.peril.core.model.battle.FinalBattleActor;
import com.forerunnergames.peril.core.model.battle.PendingBattleActor;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.people.player.PlayerModel.PlayerJoinGameStatus;
import com.forerunnergames.peril.core.model.playmap.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTimerDuration;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateTransitionCondition;
import com.forerunnergames.peril.core.model.state.events.BattleResultContinueEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultDefeatEvent;
import com.forerunnergames.peril.core.model.state.events.BattleResultVictoryEvent;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountryAssignmentEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private static final long BATTLE_RESPONSE_TIMEOUT_MS = (long) GameSettings.BATTLE_RESPONSE_TIMEOUT_SECONDS * 1000;
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final CountryOwnerModel countryOwnerModel;
  private final CountryGraphModel countryGraphModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final CardModel cardModel;
  private final PlayerTurnModel playerTurnModel;
  private final BattleModel battleModel;
  private final PlayerTurnDataCache <CacheKey> turnDataCache;
  private final GameRules rules;
  private final EventFactory eventFactory;
  private final InternalCommunicationHandler internalCommHandler;
  private final MBassador <Event> eventBus;
  private final AtomicInteger currentRound = new AtomicInteger ();

  private enum CacheKey
  {
    BATTLE_ATTACK_VECTOR,
    BATTLE_ATTACK_ORDER,
    FINAL_BATTLE_ACTOR_ATTACKER,
    FINAL_BATTLE_ACTOR_DEFENDER,
    OCCUPY_SOURCE_COUNTRY,
    OCCUPY_TARGET_COUNTRY,
    OCCUPY_PREV_OWNER,
    OCCUPY_NEW_OWNER,
    OCCUPY_MIN_ARMY_COUNT,
    OCCUPY_MAX_ARMY_COUNT,
    PLAYER_OCCUPIED_COUNTRY,
    FORTIFY_VALID_VECTORS,
    FORTIFY_SOURCE_COUNTRY_ID,
    FORTIFY_TARGET_COUNTRY_ID
  }

  GameModel (final PlayerModel playerModel,
             final PlayMapModel playMapModel,
             final CardModel cardModel,
             final PlayerTurnModel playerTurnModel,
             final BattleModel battleModel,
             final GameRules rules,
             final InternalCommunicationHandler internalCommHandler,
             final PlayerTurnDataCache <CacheKey> turnDataCache,
             final EventFactory eventFactory,
             final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (battleModel, "battleModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.cardModel = cardModel;
    this.playerTurnModel = playerTurnModel;
    this.battleModel = battleModel;
    this.rules = rules;
    this.internalCommHandler = internalCommHandler;
    this.turnDataCache = turnDataCache;
    this.eventFactory = eventFactory;
    this.eventBus = eventBus;

    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryGraphModel = playMapModel.getCountryGraphModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    continentOwnerModel = playMapModel.getContinentOwnerModel ();
    // continentGraphModel = playMapModel.getContinentGraphModel ();

    eventBus.subscribe (internalCommHandler);
  }

  public static Builder builder (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    return new Builder (rules);
  }

  public static GameModel create (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    return builder (rules).build ();
  }

  @StateEntryAction
  public void waitForGameToBegin ()
  {
    log.info ("Waiting for game to begin...");

    publish (new WaitingForPlayersToJoinGameEvent ());
  }

  @StateEntryAction
  public void beginGame ()
  {
    log.info ("Starting a new game...");

    playerModel.removeAllArmiesFromHandsOfAllPlayers ();
    playerModel.removeAllCardsFromHandsOfAllPlayers ();
    countryOwnerModel.unassignAllCountries ();
    countryArmyModel.resetAllCountries ();
    playerTurnModel.resetCurrentTurn ();
    playerTurnModel.resetTurnCount ();
    currentRound.set (0);

    publish (new BeginGameEvent ());

    // TODO Reset entire game state.
  }

  @StateEntryAction
  public void endGame ()
  {
    log.info ("Game over.");

    // TODO End the game gracefully - this can be called DURING ANY GAME STATE
  }

  @StateEntryAction
  public void beginPlayerTurn ()
  {
    log.info ("Turn begins for player [{}].", getCurrentPlayerName ());

    // clear state data cache
    turnDataCache.clearAll ();

    // clear inbound event cache
    internalCommHandler.clearEventCache ();

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (isFirstTurn ()) publish (new BeginRoundEvent (currentRound.incrementAndGet ()));
    publish (new BeginPlayerTurnEvent (currentPlayer));
    publish (new ActivePlayerChangedEvent (currentPlayer));
  }

  @StateEntryAction
  public void endPlayerTurn ()
  {
    log.info ("Turn ends for player [{}].", getCurrentPlayerName ());

    // verify win/lose status of all players
    for (final Id playerId : playerModel.getPlayerIds ())
    {
      checkPlayerGameStatus (playerId);
    }

    // check if player should draw card
    final Optional <Boolean> playerOccupiedCountry = turnDataCache.checkAndGet (CacheKey.PLAYER_OCCUPIED_COUNTRY,
                                                                                Boolean.class);
    CardPacket newPlayerCard = null;
    if (playerOccupiedCountry.isPresent () && playerOccupiedCountry.get ())
    {
      // use fortify phase for rule check since card count should never exceed 6 at the end of a turn
      // TODO: Attack phase trade-ins; for the prior statement to be true, attack-phase trade-ins must be implemented
      final Card card = cardModel.giveCard (getCurrentPlayerId (), TurnPhase.FORTIFY);
      log.debug ("Distributing card [{}] to player [{}]...", card, getCurrentPlayerPacket ());
      newPlayerCard = CardPackets.from (card);
    }

    publish (new EndPlayerTurnEvent (getCurrentPlayerPacket (), newPlayerCard));
    if (isLastTurn ()) publish (new EndRoundEvent (currentRound.get ()));

    if (turnDataCache.isSet (CacheKey.PLAYER_OCCUPIED_COUNTRY)) clearCacheValues (CacheKey.PLAYER_OCCUPIED_COUNTRY);
  }

  @StateTransitionAction
  public void skipPlayerTurn (final SkipPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.info ("Skipping turn for player [{}].", event.getPlayerName ());
  }

  @StateTransitionCondition
  public boolean verifyPlayerEndTurnRequest (final EndPlayerTurnRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket player = getCurrentPlayerPacket ();
    final Optional <PlayerPacket> sender = internalCommHandler.senderOf (event);
    if (!sender.isPresent () || player.isNot (sender.get ()))
    {
      publish (new EndPlayerTurnDeniedEvent (player, EndPlayerTurnDeniedEvent.Reason.NOT_IN_TURN));
      return false;
    }

    publish (new EndPlayerTurnSuccessEvent (player));

    return true;
  }

  @StateEntryAction
  public void determinePlayerTurnOrder ()
  {
    log.info ("Determining player turn order randomly...");

    final ImmutableSet <PlayerPacket> players = playerModel.getPlayerPackets ();
    final List <PlayerPacket> shuffledPlayers = Randomness.shuffle (players);
    final Iterator <PlayerPacket> randomPlayerItr = shuffledPlayers.iterator ();

    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!randomPlayerItr.hasNext ()) break;

      final PlayerPacket player = randomPlayerItr.next ();
      final Id playerId = playerModel.idOf (player.getName ());

      // Don't publish DefaultPlayerTurnOrderChangedEvent's for turn order mutation results because
      // the changes are temporary placeholders, and the final changes are published together in
      // DeterminePlayerTurnOrderCompleteEvent.
      playerModel.changeTurnOrderOfPlayer (playerId, turnOrder);

      log.info ("Set turn order of player [{}] to [{}].", player.getName (), turnOrder);
    }

    final ImmutableSortedSet.Builder <PlayerPacket> ordered = ImmutableSortedSet
            .orderedBy (PlayerPacket.TURN_ORDER_COMPARATOR);
    ordered.addAll (playerModel.getPlayerPackets ());
    publish (new DeterminePlayerTurnOrderCompleteEvent (ordered.build ()));
  }

  @StateEntryAction
  public void distributeInitialArmies ()
  {
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    for (final PlayerPacket player : playerModel.getTurnOrderedPlayers ())
    {
      final Id playerId = playerModel.idOf (player.getName ());
      playerModel.addArmiesToHandOf (playerId, armies);

      publish (new DefaultPlayerArmiesChangedEvent (playerModel.playerPacketWith (playerId), armies));
    }

    publish (new DistributeInitialArmiesCompleteEvent (playerModel.getPlayerPackets ()));
  }

  @StateEntryAction
  public void waitForCountryAssignmentToBegin ()
  {
    final InitialCountryAssignment assignmentMode = rules.getInitialCountryAssignment ();
    publish (new BeginPlayerCountryAssignmentEvent (assignmentMode));
    switch (assignmentMode)
    {
      case RANDOM:
      {
        log.info ("Initial country assignment = RANDOM");
        publish (new RandomlyAssignPlayerCountriesEvent ());
        break;
      }
      case MANUAL:
      {
        log.info ("Initial country assignment = MANUAL");
        publish (new BeginManualCountryAssignmentEvent ());
        break;
      }
      default:
      {
        Exceptions.throwRuntime ("Unrecognized value for initial country assignment: {}", assignmentMode);
        break;
      }
    }
  }

  @StateEntryAction
  public void randomlyAssignPlayerCountries ()
  {
    // if there are no players, just give up now!
    if (playerModel.isEmpty ())
    {
      log.info ("Skipping random country assignment... no players!");
      return;
    }

    final List <Id> countries = Randomness.shuffle (new HashSet <> (countryGraphModel.getCountryIds ()));
    final List <PlayerPacket> players = Randomness.shuffle (playerModel.getPlayerPackets ());
    final ImmutableList <Integer> playerCountryDistribution = rules
            .getInitialPlayerCountryDistribution (players.size ());

    log.info ("Randomly assigning {} countries to {} players...", countries.size (), players.size ());

    final Iterator <Id> countryItr = countries.iterator ();
    for (int i = 0; i < players.size (); ++i)
    {
      final PlayerPacket nextPlayer = players.get (i);
      final Id nextPlayerId = playerModel.idOf (nextPlayer.getName ());
      final int playerCountryCount = playerCountryDistribution.get (i);

      int assignSuccessCount = 0; // for logging purposes
      for (int count = 0; count < playerCountryCount && countryItr.hasNext (); count++)
      {
        final Id toAssign = countryItr.next ();
        MutatorResult <?> result = countryOwnerModel.requestToAssignCountryOwner (toAssign, nextPlayerId);
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", countryGraphModel.nameOf (toAssign),
                    nextPlayer, result.getFailureReason ());
          continue;
        }

        result.commitIfSuccessful ();

        result = countryArmyModel.requestToAddArmiesToCountry (toAssign, 1);
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", countryGraphModel.nameOf (toAssign),
                    nextPlayer, result.getFailureReason ());
          continue;
        }

        result.commitIfSuccessful ();

        playerModel.removeArmyFromHandOf (nextPlayerId);
        assignSuccessCount++;

        publish (new DefaultCountryArmiesChangedEvent (countryGraphModel.countryPacketWith (toAssign), 1));
        publish (new DefaultCountryOwnerChangedEvent (countryGraphModel.countryPacketWith (toAssign), nextPlayer));

        countryItr.remove ();
      }

      log.info ("Assigned {} countries to [{}].", assignSuccessCount, nextPlayer.getName ());
      final PlayerPacket updatedPlayerPacket = playerModel.playerPacketWith (nextPlayerId);
      publish (new DefaultPlayerArmiesChangedEvent (updatedPlayerPacket, -1 * assignSuccessCount));
    }

    // create map of country -> player packets for
    // PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);

    publish (new PlayerCountryAssignmentCompleteEvent (rules.getInitialCountryAssignment (), playMapViewPackets));
  }

  @StateTransitionAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerFactory playerFactory = new PlayerFactory ();
    playerFactory.newPlayerWith (event.getPlayerName (), event.getPlayerSentience ());
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (playerFactory);

    // for loop is a formality; there should only ever be one result for this case.
    for (final PlayerJoinGameStatus result : results)
    {
      final PlayerPacket player = result.getPlayer ();
      if (result.failed ())
      {
        publish (new PlayerJoinGameDeniedEvent (player.getName (), result.getFailureReason ()));
        continue;
      }

      publish (new PlayerJoinGameSuccessEvent (player, playerModel.getPlayerPackets (), rules.getPlayerLimit ()));
    }
  }

  /**
   * This method will be called after {@link InternalCommunicationHandler} has already handled the
   * {@link InternalPlayerLeaveGameEvent}.
   */
  @StateTransitionAction
  public void handlePlayerLeaveGame (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    // if the player is somehow still in the game, log a warning and return;
    // this might indicate a bug in one of the event handlers
    if (playerModel.existsPlayerWith (event.getPlayerName ()))
    {
      log.warn ("Received [{}], but player [{}] still exists.", event, event.getPlayer ());
      return;
    }
  }

  @StateEntryAction
  public void beginInitialReinforcementPhase ()
  {
    log.info ("Begin initial reinforcement phase...");

    resetTurn ();

    publish (new BeginInitialReinforcementPhaseEvent (getCurrentPlayerPacket ()));
  }

  @StateEntryAction
  public void waitForPlayersToClaimInitialCountries ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (countryOwnerModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for
      // PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);
      publish (new PlayerCountryAssignmentCompleteEvent (rules.getInitialCountryAssignment (), playMapViewPackets));
      return;
    }

    if (currentPlayer.getArmiesInHand () == 0)
    {
      log.info ("Player [{}] has no armies. Skipping...", currentPlayer);
      publish (new SkipPlayerTurnEvent (currentPlayer));
      return;
    }

    log.info ("Waiting for player [{}] to claim a country...", currentPlayer.getName ());
    publish (new PlayerClaimCountryRequestEvent (currentPlayer, countryOwnerModel.getUnownedCountries ()));
    publish (new PlayerClaimCountryWaitEvent (currentPlayer));
    publish (new ActivePlayerChangedEvent (currentPlayer));
  }

  @StateTransitionCondition
  public boolean verifyPlayerClaimCountryResponseRequest (final PlayerClaimCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    final Id currentPlayerId = playerModel.idOf (currentPlayer.getName ());

    final String claimedCountryName = event.getClaimedCountryName ();

    if (!playerModel.canRemoveArmyFromHandOf (currentPlayerId))
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName,
              PlayerClaimCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_OVERFLOW));
      republishRequestFor (event);
      return false;
    }

    if (!countryGraphModel.existsCountryWith (claimedCountryName))
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName,
              PlayerClaimCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      republishRequestFor (event);
      return false;
    }

    final Id countryId = countryGraphModel.idOf (claimedCountryName);

    final MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> res1;
    res1 = countryOwnerModel.requestToAssignCountryOwner (countryId, currentPlayerId);
    if (res1.failed ())
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName, res1.getFailureReason ()));
      republishRequestFor (event);
      return false;
    }

    final MutatorResult <AbstractPlayerChangeCountryDeniedEvent.Reason> res2;
    res2 = countryArmyModel.requestToAddArmiesToCountry (countryId, 1);
    if (res2.failed ())
    {
      publish (new PlayerClaimCountryResponseDeniedEvent (currentPlayer, claimedCountryName, res2.getFailureReason ()));
      republishRequestFor (event);
      return false;
    }

    MutatorResult.commitAllSuccessful (res1, res2);
    playerModel.removeArmyFromHandOf (currentPlayerId);

    final PlayerPacket updatedPlayer = playerModel.playerPacketWith (currentPlayerId);
    publish (new PlayerClaimCountryResponseSuccessEvent (updatedPlayer, countryGraphModel.countryPacketWith (countryId),
            1));

    return true;
  }

  @StateEntryAction
  public void waitForPlayersToReinforceInitialCountries ()
  {
    int totalArmySum = 0;
    for (final Id playerId : playerModel.getPlayerIds ())
    {
      totalArmySum += playerModel.getArmiesInHand (playerId);
    }

    if (totalArmySum == 0)
    {
      publish (new EndInitialReinforcementPhaseEvent (buildPlayMapViewFrom (playerModel, playMapModel)));
      return;
    }

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();
    final Id playerId = getCurrentPlayerId ();

    if (playerModel.getArmiesInHand (playerId) == 0)
    {
      log.trace ("Player [{}] has no armies remaining in hand. Skipping...", playerPacket);
      publish (new SkipPlayerTurnEvent (playerPacket));
      return;
    }

    log.trace ("Waiting for [{}] to place initial reinforcements...", playerPacket);

    publish (eventFactory.createReinforcementEventFor (playerId));
    publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
    publish (new ActivePlayerChangedEvent (playerPacket));
  }

  @StateTransitionCondition
  public boolean verifyPlayerInitialCountryReinforcements (final PlayerReinforceCountryRequestEvent event)
  {
    log.info ("Event received [{}]", event);

    final Id playerId = getCurrentPlayerId ();
    final int requestedReinforcements = event.getReinforcementCount ();

    if (requestedReinforcements > playerModel.getArmiesInHand (playerId))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND, event));
      return false;
    }

    if (requestedReinforcements < rules.getMinReinforcementsPlacedPerCountry ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_REINFORCEMENTS_PLACED, event));
      return false;
    }

    final String countryName = event.getCountryName ();
    if (!countryGraphModel.existsCountryWith (countryName))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST, event));
      return false;
    }

    final Id countryId = countryGraphModel.countryWith (countryName);
    if (!countryOwnerModel.isCountryOwnedBy (countryId, playerId))
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY, event));
      return false;
    }

    final MutatorResult <PlayerReinforceCountryDeniedEvent.Reason> result;
    result = countryArmyModel.requestToAddArmiesToCountry (countryId, requestedReinforcements);

    if (result.failed ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (), result.getFailureReason (), event));
      return false;
    }

    result.commitIfSuccessful ();
    playerModel.removeArmiesFromHandOf (playerId, requestedReinforcements);

    final CountryPacket countryPacket = countryGraphModel.countryPacketWith (countryId);
    publish (new PlayerReinforceCountrySuccessEvent (getCurrentPlayerPacket (), countryPacket,
            requestedReinforcements));

    return true;
  }

  @StateEntryAction
  public void beginReinforcementPhase ()
  {
    final Id playerId = getCurrentPlayerId ();

    log.info ("Begin reinforcement phase for player [{}].", getCurrentPlayerPacket ());

    // add country reinforcements and publish event
    final int countryReinforcementBonus = rules
            .calculateCountryReinforcements (countryOwnerModel.countCountriesOwnedBy (playerId));
    int continentReinforcementBonus = 0;
    final ImmutableSet <ContinentPacket> playerOwnedContinents = continentOwnerModel.getContinentsOwnedBy (playerId);
    for (final ContinentPacket cont : playerOwnedContinents)
    {
      continentReinforcementBonus += cont.getReinforcementBonus ();
    }
    final int totalReinforcementBonus = countryReinforcementBonus + continentReinforcementBonus;
    playerModel.addArmiesToHandOf (playerId, totalReinforcementBonus);

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    // publish phase begin event and trade in request
    publish (new BeginReinforcementPhaseEvent (playerPacket, countryReinforcementBonus, continentReinforcementBonus));
    publish (eventFactory.createReinforcementEventFor (playerId));
    publishTradeInEventIfNecessary ();
    publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
  }

  @StateEntryAction
  public void waitForPlayerToPlaceReinforcements ()
  {
    final Id playerId = getCurrentPlayerId ();
    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    if (playerModel.getArmiesInHand (playerId) > 0)
    {
      publish (eventFactory.createReinforcementEventFor (playerId));
      publish (new PlayerBeginReinforcementWaitEvent (playerPacket));
      log.info ("Waiting for player [{}] to place reinforcements...", playerPacket);
    }
    else
    {
      publish (new EndReinforcementPhaseEvent (playerPacket, countryOwnerModel.getCountriesOwnedBy (playerId)));
      log.info ("Player [{}] has no more armies in hand. Moving to next phase...", playerPacket);
    }
  }

  @StateTransitionCondition
  public boolean verifyPlayerReinforceCountry (final PlayerReinforceCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id playerId = getCurrentPlayerId ();

    if (cardModel.countCardsInHand (playerId) >= rules.getMinCardsInHandForTradeInReinforcePhase ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              PlayerReinforceCountryDeniedEvent.Reason.TRADE_IN_REQUIRED, event));
      return false;
    }

    // --- process country reinforcements --- //

    final String countryName = event.getCountryName ();
    final int reinforcementCount = event.getReinforcementCount ();

    MutatorResult <PlayerReinforceCountryDeniedEvent.Reason> result;
    final ImmutableSet.Builder <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> resultBuilder;
    resultBuilder = ImmutableSet.builder ();

    if (reinforcementCount > playerModel.getArmiesInHand (playerId))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND);
      resultBuilder.add (result);
    }

    if (reinforcementCount < rules.getMinReinforcementsPlacedPerCountry ())
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_REINFORCEMENTS_PLACED);
      resultBuilder.add (result);
    }

    if (!countryGraphModel.existsCountryWith (countryName))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
      resultBuilder.add (result);
    }

    final Id countryId = countryGraphModel.idOf (countryName);
    if (!countryOwnerModel.isCountryOwnedBy (countryId, playerId))
    {
      result = MutatorResult.failure (PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY);
      resultBuilder.add (result);
    }

    result = countryArmyModel.requestToAddArmiesToCountry (countryId, reinforcementCount);
    resultBuilder.add (result);

    final ImmutableSet <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> results = resultBuilder.build ();
    final Optional <MutatorResult <PlayerReinforceCountryDeniedEvent.Reason>> firstFailure;
    firstFailure = Result.firstFailedFrom (results);

    if (firstFailure.isPresent ())
    {
      publish (new PlayerReinforceCountryDeniedEvent (getCurrentPlayerPacket (),
              firstFailure.get ().getFailureReason (), event));
      return false;
    }

    // commit results
    playerModel.removeArmiesFromHandOf (playerId, reinforcementCount);
    MutatorResult.commitAllSuccessful (results.toArray (new MutatorResult <?> [results.size ()]));

    final CountryPacket countryPacket = countryGraphModel.countryPacketWith (countryId);
    publish (new PlayerReinforceCountrySuccessEvent (getCurrentPlayerPacket (), countryPacket, reinforcementCount));

    return true;
  }

  @StateExitAction
  public void endReinforcementPhase ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("End reinforcement phase for player [{}].", player);
  }

  /**
   * @return true if trade-ins are complete and state machine should advance to normal reinforcement state, false if
   *         additional trade-ins are available
   */
  @StateTransitionCondition
  public boolean verifyPlayerCardTradeIn (final PlayerTradeInCardsRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id playerId = playerModel.idOf (getCurrentPlayerName ());

    Result <PlayerTradeInCardsResponseDeniedEvent.Reason> result = Result.success ();

    final CardSetPacket tradeIn = event.getTradeIn ();

    final ImmutableSet <Card> cards = CardPackets.toCardSet (tradeIn.getCards (), cardModel);
    final CardSet cardSet = new CardSet (rules, cards);
    if (!cardSet.isEmpty () && !cardSet.isMatch ())
    {
      result = Result.failure (PlayerTradeInCardsResponseDeniedEvent.Reason.INVALID_CARD_SET);
    }

    final int cardTradeInBonus = cardModel.getNextTradeInBonus ();

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      result = cardModel.requestTradeInCards (playerId, cardSet.match (), TurnPhase.REINFORCE);
    }

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      playerModel.addArmiesToHandOf (playerId, cardTradeInBonus);
    }
    else if (result.failed ())
    {
      publish (new PlayerTradeInCardsResponseDeniedEvent (getCurrentPlayerPacket (), result.getFailureReason ()));
      return false;
    }

    publish (new PlayerTradeInCardsResponseSuccessEvent (getCurrentPlayerPacket (), event.getTradeIn (),
            cardTradeInBonus, cardModel.getNextTradeInBonus ()));

    final boolean shouldWaitForNextTradeIn = publishTradeInEventIfNecessary ();
    return !shouldWaitForNextTradeIn;
  }

  @StateEntryAction
  public void beginAttackPhase ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    log.info ("Begin attack phase for player [{}].", currentPlayer);
    publish (new BeginAttackPhaseEvent (currentPlayer));
  }

  @StateEntryAction
  public void waitForPlayerToSelectAttackVector ()
  {
    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> builder = ImmutableMultimap.builder ();
    for (final CountryPacket country : countryOwnerModel.getCountriesOwnedBy (getCurrentPlayerId ()))
    {
      final Id countryId = countryGraphModel.countryWith (country.getName ());
      builder.putAll (country, battleModel.getValidAttackTargetsFor (countryId, playMapModel));
    }

    final PlayerPacket playerPacket = getCurrentPlayerPacket ();

    publish (new PlayerBeginAttackEvent (playerPacket, builder.build ()));
    publish (new PlayerBeginAttackWaitEvent (playerPacket));
  }

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
  }

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

  @StateTimerDuration
  public long getBattleResponseTimeoutMs ()
  {
    return BATTLE_RESPONSE_TIMEOUT_MS;
  }

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

  @StateExitAction
  public void endAttackPhase ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();
    log.info ("End attack phase for player [{}].", player);
    publish (new EndAttackPhaseEvent (player));
  }

  @StateEntryAction
  public void beginFortifyPhase ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    final Id currentPlayerId = getCurrentPlayerId ();
    final ImmutableSet <CountryPacket> ownedCountries = countryOwnerModel.getCountriesOwnedBy (currentPlayerId);
    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> validFortifyVectorBuilder = ImmutableSetMultimap
            .builder ();
    for (final CountryPacket country : ownedCountries)
    {
      if (!country.hasAtLeastNArmies (rules.getMinArmiesOnSourceCountryForFortify ())) continue;
      final Id countryId = countryGraphModel.countryWith (country.getName ());
      final ImmutableSet <Id> adjCountries = countryGraphModel.getAdjacentNodes (countryId);
      for (final Id adjCountry : adjCountries)
      {
        if (!countryOwnerModel.isCountryOwnedBy (adjCountry, currentPlayerId)) continue;
        validFortifyVectorBuilder.put (country, countryGraphModel.countryPacketWith (adjCountry));
      }
    }

    final ImmutableMultimap <CountryPacket, CountryPacket> validFortifyVectors = validFortifyVectorBuilder.build ();
    if (validFortifyVectors.isEmpty ())
    {
      publish (new SkipFortifyPhaseEvent (getCurrentPlayerPacket ()));
      return;
    }

    turnDataCache.put (CacheKey.FORTIFY_VALID_VECTORS, validFortifyVectors);

    log.info ("Begin fortify phase for player [{}].", currentPlayer);

    publish (new BeginFortifyPhaseEvent (currentPlayer));
    publish (new PlayerBeginFortificationWaitEvent (currentPlayer));
  }

  @StateExitAction
  public void endFortifyPhase ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("End fortify phase for player [{}].", player);

    publish (new EndFortifyPhaseEvent (player));
  }

  @StateEntryAction
  @SuppressWarnings ("unchecked")
  public void waitForPlayerToSelectFortifyVector ()
  {
    // Fixes PERIL-842 ("SkipFortifyPhaseEvent Crashes Server")
    // We're in the middle of skipping fortification phase, so do nothing.
    if (turnDataCache.isNotSet (CacheKey.FORTIFY_VALID_VECTORS)) return;

    final PlayerPacket player = getCurrentPlayerPacket ();

    log.info ("Waiting for player [{}] to select a fortification vector...", player);

    final ImmutableMultimap <CountryPacket, CountryPacket> validFortifyVectors = turnDataCache
            .get (CacheKey.FORTIFY_VALID_VECTORS, ImmutableMultimap.class);

    publish (new PlayerBeginFortificationEvent (player, validFortifyVectors));
  }

  @StateTransitionCondition
  public boolean verifyPlayerFortifyVectorSelection (final PlayerSelectFortifyVectorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id currentPlayerId = getCurrentPlayerId ();
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (internalCommHandler.isNotSenderOf (event, currentPlayer))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.PLAYER_NOT_IN_TURN));
      return false;
    }

    if (!countryGraphModel.existsCountryWith (event.getSourceCountry ()))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.SOURCE_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    if (!countryGraphModel.existsCountryWith (event.getTargetCountry ()))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.TARGET_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    final Id sourceCountryId = countryGraphModel.countryWith (event.getSourceCountry ());
    final Id targetCountryId = countryGraphModel.countryWith (event.getTargetCountry ());

    if (!countryOwnerModel.isCountryOwnedBy (sourceCountryId, currentPlayerId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY));
      return false;
    }

    if (!countryOwnerModel.isCountryOwnedBy (targetCountryId, currentPlayerId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY));
      return false;
    }

    if (!countryGraphModel.areAdjacent (sourceCountryId, targetCountryId))
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT));
      return false;
    }

    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountryId);
    final int targetCountryArmyCount = countryArmyModel.getArmyCountFor (targetCountryId);

    if (sourceCountryArmyCount < rules.getMinArmiesOnSourceCountryForFortify ())
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.SOURCE_COUNTRY_ARMY_UNDERFLOW));
      return false;
    }

    if (targetCountryArmyCount > rules.getMaxArmiesOnTargetCountryForFortify ())
    {
      publish (new PlayerSelectFortifyVectorDeniedEvent (currentPlayer,
              PlayerSelectFortifyVectorDeniedEvent.Reason.TARGET_COUNTRY_ARMY_OVERFLOW));
      return false;
    }

    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountryId);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountryId);
    final PlayerPacket playerPacket = getCurrentPlayerPacket ();
    final int minDeltaArmyCount = rules.getMinFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);
    final int maxDeltaArmyCount = rules.getMaxFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);

    publish (new PlayerSelectFortifyVectorSuccessEvent (playerPacket, sourceCountryPacket, targetCountryPacket));
    publish (new PlayerIssueFortifyOrderEvent (playerPacket, sourceCountryPacket, targetCountryPacket,
            minDeltaArmyCount, maxDeltaArmyCount));
    publish (new PlayerIssueFortifyOrderWaitEvent (playerPacket, sourceCountryPacket, targetCountryPacket,
            minDeltaArmyCount, maxDeltaArmyCount));

    turnDataCache.put (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, sourceCountryId);
    turnDataCache.put (CacheKey.FORTIFY_TARGET_COUNTRY_ID, targetCountryId);

    return true;
  }

  @StateTransitionCondition
  public boolean verifyPlayerFortifyOrder (final PlayerOrderFortifyRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    checkCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (internalCommHandler.isNotSenderOf (event, currentPlayer))
    {
      publish (new PlayerOrderFortifyDeniedEvent (currentPlayer,
              PlayerOrderFortifyDeniedEvent.Reason.PLAYER_NOT_IN_TURN));
      return false;
    }

    final Id sourceCountry = turnDataCache.get (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, Id.class);
    final Id targetCountry = turnDataCache.get (CacheKey.FORTIFY_TARGET_COUNTRY_ID, Id.class);
    final int deltaArmyCount = event.getDeltaArmyCount ();
    final int sourceCountryArmyCount = countryArmyModel.getArmyCountFor (sourceCountry);
    final int targetCountryArmyCount = countryArmyModel.getArmyCountFor (targetCountry);
    final int minDeltaArmyCount = rules.getMinFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);
    final int maxDeltaArmyCount = rules.getMaxFortifyDeltaArmyCount (sourceCountryArmyCount, targetCountryArmyCount);

    if (deltaArmyCount < minDeltaArmyCount)
    {
      publish (new PlayerOrderFortifyDeniedEvent (currentPlayer,
              PlayerOrderFortifyDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_UNDERFLOW));
      return false;
    }

    if (deltaArmyCount > maxDeltaArmyCount)
    {
      publish (new PlayerOrderFortifyDeniedEvent (currentPlayer,
              PlayerOrderFortifyDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_OVERFLOW));
      return false;
    }

    final MutatorResult <?> res1, res2;
    res1 = countryArmyModel.requestToRemoveArmiesFromCountry (sourceCountry, deltaArmyCount);
    res2 = countryArmyModel.requestToAddArmiesToCountry (targetCountry, deltaArmyCount);

    final Optional <MutatorResult <?>> failed = Result.firstGenericFailedFrom (res1, res2);
    if (failed.isPresent ())
    {
      // failure result from model class suggests some kind of serious state inconsistency
      Exceptions.throwIllegalState ("Failed to change country army states [Reason: {}].",
                                    failed.get ().getFailureReason ());
    }

    MutatorResult.commitAllSuccessful (res1, res2);

    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountry);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountry);
    publish (new DefaultCountryArmiesChangedEvent (sourceCountryPacket, -deltaArmyCount));
    publish (new DefaultCountryArmiesChangedEvent (targetCountryPacket, deltaArmyCount));
    publish (new PlayerOrderFortifySuccessEvent (currentPlayer, sourceCountryPacket, targetCountryPacket,
            deltaArmyCount));

    clearCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    return true;
  }

  public boolean verifyPlayerCancelFortifyVector (final PlayerCancelFortifyRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Id sourceCountry = turnDataCache.get (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, Id.class);
    final Id targetCountry = turnDataCache.get (CacheKey.FORTIFY_TARGET_COUNTRY_ID, Id.class);
    final CountryPacket sourceCountryPacket = countryGraphModel.countryPacketWith (sourceCountry);
    final CountryPacket targetCountryPacket = countryGraphModel.countryPacketWith (targetCountry);

    final PlayerPacket player = getCurrentPlayerPacket ();
    final Optional <PlayerPacket> sender = internalCommHandler.senderOf (event);
    if (!sender.isPresent () || player.isNot (sender.get ()))
    {
      publish (new PlayerCancelFortifyDeniedEvent (player, sourceCountryPacket, targetCountryPacket,
              PlayerCancelFortifyDeniedEvent.Reason.NOT_IN_TURN));
      return false;
    }

    publish (new PlayerCancelFortifySuccessEvent (player, sourceCountryPacket, targetCountryPacket));

    clearCacheValues (CacheKey.FORTIFY_SOURCE_COUNTRY_ID, CacheKey.FORTIFY_TARGET_COUNTRY_ID);

    return true;
  }

  @StateExitAction
  public void advancePlayerTurn ()
  {
    playerTurnModel.advance ();
  }

  @StateExitAction
  public void resetTurn ()
  {
    playerTurnModel.resetCurrentTurn ();
  }

  @StateTransitionCondition
  public boolean isFull ()
  {
    return playerModel.isFull ();
  }

  @StateTransitionCondition
  public boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  public boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  public boolean playerCountIs (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIs (count);
  }

  public boolean playerLimitIs (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIs (limit);
  }

  public int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  public int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
  }

  public boolean isFirstTurn ()
  {
    return playerTurnModel.isFirstTurn ();
  }

  public boolean isLastTurn ()
  {
    return playerTurnModel.isLastTurn ();
  }

  public PlayerPacket getCurrentPlayerPacket ()
  {
    return playerModel.playerPacketWith (playerTurnModel.getCurrentTurn ());
  }

  public String getCurrentPlayerName ()
  {
    return playerModel.nameOf (getCurrentPlayerId ());
  }

  public Id getCurrentPlayerId ()
  {
    return playerModel.playerWith (playerTurnModel.getCurrentTurn ());
  }

  public void dumpDataCacheToLog ()
  {
    log.debug ("CurrentTurn: {} | Player: [{}] | Cache dump: [{}]", playerTurnModel.getCurrentTurn (),
               getCurrentPlayerId (), turnDataCache);
  }

  private static ImmutableMap <CountryPacket, PlayerPacket> buildPlayMapViewFrom (final PlayerModel playerModel,
                                                                                  final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final CountryGraphModel countryGraphModel = playMapModel.getCountryGraphModel ();
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();

    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapView = ImmutableMap.builder ();
    for (final Id countryId : countryGraphModel)
    {
      if (!countryOwnerModel.isCountryOwned (countryId)) continue;

      final Id ownerId = countryOwnerModel.ownerOf (countryId);
      playMapView.put (countryGraphModel.countryPacketWith (countryId), playerModel.playerPacketWith (ownerId));
    }
    return playMapView.build ();
  }

  private boolean publishTradeInEventIfNecessary ()
  {
    final Id playerId = getCurrentPlayerId ();
    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (playerId);
    final boolean shouldPublish = !matches.isEmpty ();
    if (shouldPublish) publish (eventFactory.createCardTradeInEventFor (playerId, matches, TurnPhase.REINFORCE));
    return shouldPublish;
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

  private void publish (final Event event)
  {
    log.trace ("Publishing event [{}]", event);
    eventBus.publish (event);
  }

  @Nullable
  private <T extends PlayerInputRequestEvent> T getOriginalRequestFor (final ResponseRequestEvent event,
                                                                       final Class <T> originalRequestType)
  {
    final Optional <PlayerInputRequestEvent> originalRequest = internalCommHandler.requestFor (event);
    if (!originalRequest.isPresent ())
    {
      log.warn ("Unable to find request event matching response [{}].", event);
      return null;
    }

    return originalRequestType.cast (originalRequest.get ());
  }

  private void republishRequestFor (final ResponseRequestEvent event)
  {
    final Optional <PlayerInputRequestEvent> originalRequest = internalCommHandler.requestFor (event);
    if (!originalRequest.isPresent ())
    {
      log.warn ("Unable to find request event matching response [{}].", event);
      return;
    }

    publish (originalRequest.get ());
  }

  // checks whether or not a player has won or lost the game in the current game state
  private void checkPlayerGameStatus (final Id playerId)
  {
    final int playerCountryCount = countryOwnerModel.countCountriesOwnedBy (playerId);
    if (playerCountryCount < rules.getMinPlayerCountryCount ())
    {
      publish (new PlayerLoseGameEvent (playerModel.playerPacketWith (playerId)));
      final ImmutableSet <PlayerModel.PlayerTurnOrderMutation> turnOrderMutations = playerModel.remove (playerId);
      playerTurnModel.decrementTurnCount ();
      for (final PlayerModel.PlayerTurnOrderMutation mutation : turnOrderMutations)
      {
        publish (new DefaultPlayerTurnOrderChangedEvent (mutation.getPlayer (), mutation.getOldTurnOrder ()));
      }
      return;
    }

    if (playerCountryCount >= rules.getWinningCountryCount ())
    {
      // player won! huzzah!
      publish (new PlayerWinGameEvent (playerModel.playerPacketWith (playerId)));
      // end the game
      publish (new EndGameEvent ());
    }
  }

  /**
   * Checks that the given keys have set values in the turn data cache. An exception is thrown if any of the given keys
   * are not set.
   */
  private void checkCacheValues (final CacheKey... keys)
  {
    assert keys != null;

    for (final CacheKey key : keys)
    {
      if (turnDataCache.isNotSet (key))
      {
        Exceptions.throwIllegalState ("No value for {} set in turn data cache.", key);
      }
    }
  }

  private void clearCacheValues (final CacheKey... keys)
  {
    assert keys != null;

    for (final CacheKey key : keys)
    {
      if (turnDataCache.isNotSet (key))
      {
        log.warn ("Cannot clear value for {} from turn data cache; no value currently set.", key);
        continue;
      }
      turnDataCache.clear (key);
    }
  }

  public static class Builder
  {
    private final GameRules gameRules;
    private PlayMapModel playMapModel;
    private PlayerModel playerModel;
    private CardModel cardModel;
    private PlayerTurnModel playerTurnModel;
    private BattleModel battleModel;
    private PlayerTurnDataCache <CacheKey> turnDataCache;
    private InternalCommunicationHandler internalCommHandler;
    private MBassador <Event> eventBus = EventBusFactory.create ();

    public GameModel build ()
    {
      if (internalCommHandler == null)
      {
        internalCommHandler = new InternalCommunicationHandler (playerModel, playMapModel, playerTurnModel, eventBus);
      }

      final EventFactory eventFactory = new DefaultEventFactory (playerModel, playMapModel, cardModel, gameRules);
      return new GameModel (playerModel, playMapModel, cardModel, playerTurnModel, battleModel, gameRules,
              internalCommHandler, turnDataCache, eventFactory, eventBus);
    }

    public Builder playMapModel (final PlayMapModel playMapModel)
    {
      Arguments.checkIsNotNull (playMapModel, "playMapModel");

      this.playMapModel = playMapModel;
      return this;
    }

    public Builder playerModel (final PlayerModel playerModel)
    {
      Arguments.checkIsNotNull (playerModel, "playerModel");

      this.playerModel = playerModel;
      return this;
    }

    public Builder cardModel (final CardModel cardModel)
    {
      Arguments.checkIsNotNull (cardModel, "cardModel");

      this.cardModel = cardModel;
      return this;
    }

    public Builder playerTurnModel (final PlayerTurnModel playerTurnModel)
    {
      Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");

      this.playerTurnModel = playerTurnModel;
      return this;
    }

    public Builder battleModel (final BattleModel battleModel)
    {
      Arguments.checkIsNotNull (battleModel, "battleModel");

      this.battleModel = battleModel;
      return this;
    }

    public Builder turnDataCache (final PlayerTurnDataCache <CacheKey> turnDataCache)
    {
      Arguments.checkIsNotNull (turnDataCache, "turnDataCache");

      this.turnDataCache = turnDataCache;
      return this;
    }

    public Builder internalComms (final InternalCommunicationHandler internalCommHandler)
    {
      Arguments.checkIsNotNull (internalCommHandler, "internalCommHandler");

      this.internalCommHandler = internalCommHandler;
      return this;
    }

    public Builder eventBus (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      this.eventBus = eventBus;
      return this;
    }

    private Builder (final GameRules gameRules)
    {
      Arguments.checkIsNotNull (gameRules, "gameRules");

      this.gameRules = gameRules;
      final CountryFactory defaultCountryFactory = CountryFactory
              .generateDefaultCountries (gameRules.getTotalCountryCount ());
      final ContinentFactory emptyContinentFactory = new ContinentFactory ();
      final CountryGraphModel disjointCountryGraph = CountryGraphModel.disjointCountryGraphFrom (defaultCountryFactory);
      playMapModel = new DefaultPlayMapModelFactory (gameRules)
              .create (disjointCountryGraph,
                       ContinentGraphModel.disjointContinentGraphFrom (emptyContinentFactory, disjointCountryGraph));
      playerModel = new DefaultPlayerModel (gameRules);
      cardModel = new DefaultCardModel (gameRules, playerModel, ImmutableSet. <Card> of ());
      playerTurnModel = new DefaultPlayerTurnModel (gameRules);
      battleModel = new DefaultBattleModel (playMapModel);
      turnDataCache = new PlayerTurnDataCache <> ();
    }
  }
}
