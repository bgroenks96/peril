package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.events.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerAttackCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerDefendCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerEndAttackPhaseResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerFortifyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerOccupyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerAttackCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerDefendCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerAttackCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerDefendCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountriesResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.PlayerTurnDataCache.CacheKey;
import com.forerunnergames.peril.core.model.battle.AttackOrder;
import com.forerunnergames.peril.core.model.battle.BattleActor;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.BattlePackets;
import com.forerunnergames.peril.core.model.battle.BattleResult;
import com.forerunnergames.peril.core.model.battle.DefaultBattleActor;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel.PlayerJoinGameStatus;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.state.StateEntryAction;
import com.forerunnergames.peril.core.model.state.StateTransitionAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountrySelectionEvent;
import com.forerunnergames.peril.core.model.state.events.EndGameEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Exceptions;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final CountryOwnerModel countryOwnerModel;
  private final CountryMapGraphModel countryMapGraphModel;
  private final CountryArmyModel countryArmyModel;
  private final ContinentOwnerModel continentOwnerModel;
  private final CardModel cardModel;
  private final PlayerTurnModel playerTurnModel;
  private final BattleModel battleModel;
  private final PlayerTurnDataCache turnDataCache;
  private final GameRules rules;
  private final InternalCommunicationHandler internalCommHandler;
  private final MBassador <Event> eventBus;

  GameModel (final PlayerModel playerModel,
             final PlayMapModel playMapModel,
             final CardModel cardModel,
             final PlayerTurnModel playerTurnModel,
             final BattleModel battleModel,
             final GameRules rules,
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
    this.eventBus = eventBus;

    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    continentOwnerModel = playMapModel.getContinentOwnerModel ();
    // continentMapGraphModel = playMapModel.getContinentMapGraphModel ();

    internalCommHandler = new InternalCommunicationHandler (playerModel, playMapModel, playerTurnModel, eventBus);
    turnDataCache = new PlayerTurnDataCache ();

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

  @StateMachineAction
  @StateTransitionAction
  public void beginGame ()
  {
    log.info ("Starting a new game...");

    playerModel.removeAllArmiesFromHandsOfAllPlayers ();
    countryOwnerModel.unassignAllCountries ();

    // TODO Clear all country armies.
    // TODO Reset entire game state.
  }

  @StateMachineAction
  @StateEntryAction
  public void endGame ()
  {
    log.info ("Game over.");

    // TODO End the game gracefully - this can be called DURING ANY GAME STATE
  }

  public void beginPlayerTurn ()
  {
    log.info ("Turn begins for player [{}].", getCurrentPlayerPacket ().getName ());

    // clear state data cache
    turnDataCache.clearAll ();

    eventBus.publish (new BeginPlayerTurnEvent (getCurrentPlayerPacket ()));
  }

  public void endPlayerTurn ()
  {
    log.info ("Turn ends for player [{}].", getCurrentPlayerPacket ().getName ());

    // verify win/lose status of all players
    for (final Id playerId : playerModel.getPlayerIds ())
    {
      checkPlayerGameStatus (playerId);
    }

    playerTurnModel.advance ();

    eventBus.publish (new EndPlayerTurnEvent (getCurrentPlayerPacket ()));
  }

  @StateMachineAction
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
      playerModel.changeTurnOrderOfPlayer (playerId, turnOrder);

      log.info ("Set turn order of player [{}] to [{}].", player.getName (), turnOrder);
    }

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent (playerModel.getPlayerPackets ()));
  }

  @StateMachineAction
  @StateEntryAction
  public void distributeInitialArmies ()
  {
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    // Create a status message listing which player received how many armies.
    final StringBuilder statusMessageBuilder = new StringBuilder ();
    for (final PlayerPacket player : playerModel.getTurnOrderedPlayers ())
    {
      final Id playerId = playerModel.idOf (player.getName ());
      playerModel.addArmiesToHandOf (playerId, armies);

      eventBus.publish (new PlayerArmiesChangedEvent (player, armies));

      // @formatter:off
      statusMessageBuilder
              .append (player.getName ())
              .append (" received ")
              .append (armies)
              .append (" armies.\n");
      // @formatter:on
    }
    if (statusMessageBuilder.length () > 0) statusMessageBuilder.deleteCharAt (statusMessageBuilder.length () - 1);

    eventBus.publish (new DistributeInitialArmiesCompleteEvent (playerModel.getPlayerPackets ()));
  }

  @StateMachineAction
  @StateEntryAction
  public void waitForCountrySelectionToBegin ()
  {
    switch (rules.getInitialCountryAssignment ())
    {
      case RANDOM:
      {
        log.info ("Initial country assignment = RANDOM");
        eventBus.publish (new RandomlyAssignPlayerCountriesEvent ());
        break;
      }
      case MANUAL:
      {
        log.info ("Initial country assignment = MANUAL");
        eventBus.publish (new BeginManualCountrySelectionEvent ());
        break;
      }
      default:
      {
        log.info ("Unrecognized value for InitialCountryAssignment.");
        break;
      }
    }
  }

  @StateMachineAction
  @StateEntryAction
  public void randomlyAssignPlayerCountries ()
  {
    // if there are no players, just give up now!
    if (playerModel.isEmpty ())
    {
      log.info ("Skipping random country assignment... no players!");
      return;
    }

    final List <Id> countries = Randomness.shuffle (new HashSet <> (countryMapGraphModel.getCountryIds ()));
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
        final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
        result = countryOwnerModel.requestToAssignCountryOwner (toAssign, nextPlayerId);
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", countryMapGraphModel.nameOf (toAssign),
                    nextPlayer.getName (), result.getFailureReason ());
        }
        else
        {
          playerModel.removeArmiesFromHandOf (nextPlayerId, 1);
          assignSuccessCount++;
        }
        countryItr.remove ();
      }

      log.info ("Assigned {} countries to [{}].", assignSuccessCount, nextPlayer.getName ());
      eventBus.publish (new PlayerArmiesChangedEvent (nextPlayer, -1 * assignSuccessCount));
    }

    // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);

    eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
  }

  @StateMachineAction
  @StateTransitionAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerFactory playerFactory = new PlayerFactory ();
    playerFactory.newPlayerWith (event.getPlayerName ());
    final ImmutableSet <PlayerJoinGameStatus> results = playerModel.requestToAdd (playerFactory);

    // for loop is a formality; there should only ever be one result for this case.
    for (final PlayerJoinGameStatus result : results)
    {
      final PlayerPacket player = result.getPlayer ();
      if (result.failed ())
      {
        eventBus.publish (new PlayerJoinGameDeniedEvent (player.getName (), result.getFailureReason ()));
        continue;
      }

      eventBus.publish (new PlayerJoinGameSuccessEvent (player));
    }
  }

  /**
   * This method will be called after {@link InternalCommunicationHandler} has already handled the
   * {@link InternalPlayerLeaveGameEvent}.
   */
  @StateMachineAction
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

  @StateMachineAction
  @StateEntryAction
  public void waitForPlayersToSelectInitialCountries ()
  {
    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    if (countryOwnerModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);
      eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
      return;
    }

    log.info ("Waiting for player [{}] to select a country...", currentPlayer.getName ());
    eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer, countryOwnerModel.getUnownedCountries ()));
  }

  @StateMachineCondition
  public boolean verifyPlayerClaimCountrySelectionRequest (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();
    final Id currentPlayerId = playerModel.idOf (currentPlayer.getName ());

    final String selectedCountryName = event.getSelectedCountryName ();

    if (!countryMapGraphModel.existsCountryWith (selectedCountryName))
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (currentPlayer, selectedCountryName,
              PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer, countryOwnerModel.getUnownedCountries ()));
      return false;
    }

    final Id countryId = countryMapGraphModel.idOf (selectedCountryName);

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    result = countryOwnerModel.requestToAssignCountryOwner (countryId, currentPlayerId);
    if (result.failed ())
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (currentPlayer, selectedCountryName,
              result.getFailureReason ()));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer, countryOwnerModel.getUnownedCountries ()));
      return false;
    }

    eventBus.publish (new PlayerSelectCountryResponseSuccessEvent (currentPlayer, selectedCountryName));
    eventBus.publish (new PlayerArmiesChangedEvent (currentPlayer, -1));

    playerTurnModel.advance ();

    return true;
  }

  @StateMachineAction
  @StateEntryAction
  public void beginReinforcementPhase ()
  {
    final PlayerPacket player = getCurrentPlayerPacket ();
    final Id playerId = playerModel.idOf (player.getName ());

    log.info ("Begin reinforcement phase for player [{}].", player);

    eventBus.publish (new BeginReinforcementPhaseEvent (player));

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

    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (playerId);
    final int cardCount = cardModel.countCardsInHand (playerId);
    final ImmutableSet <CountryPacket> playerOwnedCountries = countryOwnerModel.getCountriesOwnedBy (playerId);
    final ImmutableSet <CardSetPacket> matchPackets = CardPackets.fromCardMatchSet (matches);

    eventBus.publish (new PlayerArmiesChangedEvent (player, totalReinforcementBonus));
    // publish reinforcement request
    eventBus.publish (new PlayerReinforceCountriesRequestEvent (player, countryReinforcementBonus,
            continentReinforcementBonus, cardModel.getNextTradeInBonus (), playerOwnedCountries, matchPackets,
            cardCount > rules.getMaxCardsInHand (TurnPhase.REINFORCE)));
    log.info ("Waiting for player [{}] to place reinforcements...", player);
  }

  @StateMachineCondition
  public boolean verifyPlayerCountryReinforcements (final PlayerReinforceCountriesResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final PlayerPacket player = getCurrentPlayerPacket ();
    final Id playerId = playerModel.idOf (player.getName ());

    Result <PlayerReinforceCountriesResponseDeniedEvent.Reason> result = Result.success ();

    // --- process card trade-ins --- //

    final CardSetPacket tradeIn = event.getTradeIn ();

    final ImmutableSet <Card> cards = CardPackets.toCardSet (tradeIn.getCards (), cardModel);
    final CardSet cardSet = new CardSet (rules, cards);
    if (!cardSet.isEmpty () && !cardSet.isMatch ())
    {
      result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.INVALID_CARD_SET);
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
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (player, result.getFailureReason ()));
      return false;
    }

    // --- process country reinforcements --- //

    final ImmutableMap <String, Integer> reinforcedCountries = event.getReinforcedCountries ();

    int totalReinforcementCount = 0;
    for (final int armyCount : reinforcedCountries.values ())
    {
      totalReinforcementCount += armyCount;
    }
    if (totalReinforcementCount > player.getArmiesInHand ())
    {
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (player,
              PlayerReinforceCountriesResponseDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
      return false;
    }

    for (final String countryName : reinforcedCountries.keySet ())
    {
      if (!countryMapGraphModel.existsCountryWith (countryName))
      {
        result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
        break;
      }
      final CountryPacket country = countryMapGraphModel.countryPacketWith (countryName);
      final Id countryId = countryMapGraphModel.idOf (country.getName ());
      if (!countryOwnerModel.isCountryOwnedBy (countryId, playerId))
      {
        result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY);
        break;
      }
      final int reinforcementCount = reinforcedCountries.get (countryName);
      result = countryArmyModel.requestToAddArmiesToCountry (countryId, reinforcementCount);
      playerModel.removeArmiesFromHandOf (playerId, reinforcementCount);
    }

    if (result.failed ())
    {
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (player, result.getFailureReason ()));
      return false;
    }

    eventBus.publish (new PlayerReinforceCountriesResponseSuccessEvent (player));
    eventBus.publish (new PlayerArmiesChangedEvent (player, cardTradeInBonus - totalReinforcementCount));

    return true;
  }

  @StateMachineAction
  @StateEntryAction
  public void beginAttackPhase ()
  {
    final Id player = getCurrentPlayerId ();

    log.info ("Begin attack phase for player [{}].", player);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    eventBus.publish (new BeginAttackPhaseEvent (currentPlayer));

    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> builder = ImmutableMultimap.builder ();
    for (final CountryPacket country : countryOwnerModel.getCountriesOwnedBy (player))
    {
      final Id countryId = countryMapGraphModel.countryWith (country.getName ());
      builder.putAll (country, battleModel.getValidAttackTargetsFor (countryId, playMapModel));
    }

    eventBus.publish (new PlayerAttackCountryRequestEvent (currentPlayer, builder.build ()));
  }

  @StateMachineCondition
  public boolean verifyPlayerAttackOrder (final PlayerAttackCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Id currentPlayer = getCurrentPlayerId ();
    final PlayerPacket currentPlayerPacket = getCurrentPlayerPacket ();

    final String sourceCountryName = event.getSourceCountryName ();
    final Id sourceCountry = countryMapGraphModel.countryWith (sourceCountryName);
    final String targetCountryName = event.getTargetCounryName ();
    final Id targetCountry = countryMapGraphModel.countryWith (targetCountryName);
    final CountryPacket targetCountryPacket = countryMapGraphModel.countryPacketWith (targetCountry);

    if (!countryMapGraphModel.existsCountryWith (sourceCountryName))
    {
      eventBus.publish (new PlayerAttackCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerAttackCountryResponseDeniedEvent.Reason.SOURCE_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    if (!countryMapGraphModel.existsCountryWith (targetCountryName))
    {
      eventBus.publish (new PlayerAttackCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerAttackCountryResponseDeniedEvent.Reason.TARGET_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    final int dieCount = event.getAttackerDieCount ();

    final DataResult <AttackOrder, PlayerAttackCountryResponseDeniedEvent.Reason> result;
    result = battleModel.newPlayerAttackOrder (currentPlayer, sourceCountry, targetCountry, dieCount, playMapModel);
    if (result.failed ())
    {
      eventBus.publish (new PlayerAttackCountryResponseDeniedEvent (currentPlayerPacket, result.getFailureReason ()));
      return false;
    }

    // store pending attack order id
    turnDataCache.put (CacheKey.BATTLE_PENDING_ATTACK_ORDER, result.getReturnValue ());

    final PlayerPacket defendingPlayer = playerModel.playerPacketWith (countryOwnerModel.ownerOf (targetCountry));
    final BattleActor attacker = new DefaultBattleActor (currentPlayer, sourceCountry, dieCount);
    turnDataCache.put (CacheKey.BATTLE_ATTACKER_DATA, attacker);

    // send out request event to defender
    eventBus.publish (new PlayerDefendCountryRequestEvent (defendingPlayer, targetCountryPacket,
            BattlePackets.from (attacker, playerModel, countryMapGraphModel)));
    return true;
  }

  @StateMachineCondition
  public boolean verifyPlayerEndAttackPhase (final PlayerEndAttackPhaseResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayerPacket ();

    eventBus.publish (new PlayerEndAttackPhaseResponseSuccessEvent (currentPlayer));

    return true;
  }

  @StateMachineCondition
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

    // fail if cache values are not set; this would indicate a pretty serious bug in the state
    // machine logic
    checkCacheValues (CacheKey.BATTLE_PENDING_ATTACK_ORDER, CacheKey.BATTLE_ATTACKER_DATA);

    final AttackOrder attackOrder = turnDataCache.get (CacheKey.BATTLE_PENDING_ATTACK_ORDER, AttackOrder.class);
    final BattleActor attacker = turnDataCache.get (CacheKey.BATTLE_ATTACKER_DATA, BattleActor.class);
    final Id defendingPlayerId = countryOwnerModel.ownerOf (attackOrder.getTargetCountry ());
    final PlayerPacket defendingPlayer = playerModel.playerPacketWith (defendingPlayerId);
    final CountryPacket defendingCountry = countryMapGraphModel.countryPacketWith (attackOrder.getTargetCountry ());

    if (!defendingPlayer.equals (sender.get ()))
    {
      log.warn ("Sender of event [{}] does not match registered defending player [{}].", sender.get (),
                defendingPlayer);
      return false;
    }

    final int dieCount = event.getDefenderDieCount ();
    if (dieCount < rules.getMinAttackerDieCount (defendingCountry.getArmyCount ())
            || dieCount > rules.getMaxAttackerDieCount (defendingCountry.getArmyCount ()))
    {
      eventBus.publish (new PlayerDefendCountryResponseDeniedEvent (sender.get (),
              PlayerDefendCountryResponseDeniedEvent.Reason.INVALID_DIE_COUNT));
      // re-publish request event on failure
      eventBus.publish (new PlayerDefendCountryRequestEvent (defendingPlayer, defendingCountry,
              BattlePackets.from (attacker, playerModel, countryMapGraphModel)));
      return false;
    }

    eventBus.publish (new PlayerDefendCountryResponseSuccessEvent (defendingPlayer, defendingCountry, dieCount,
            BattlePackets.from (attacker, playerModel, countryMapGraphModel)));

    final BattleActor defender = new DefaultBattleActor (defendingPlayerId, attackOrder.getTargetCountry (), dieCount);
    turnDataCache.put (CacheKey.BATTLE_DEFENDER_DATA, defender);

    return true;
  }

  @StateMachineAction
  public void generateBattleResult ()
  {
    checkCacheValues (CacheKey.BATTLE_ATTACKER_DATA, CacheKey.BATTLE_DEFENDER_DATA,
                      CacheKey.BATTLE_PENDING_ATTACK_ORDER);

    final BattleActor attacker = turnDataCache.get (CacheKey.BATTLE_ATTACKER_DATA, BattleActor.class);
    final BattleActor defender = turnDataCache.get (CacheKey.BATTLE_DEFENDER_DATA, BattleActor.class);
    // create packet types for passing information to log message
    final BattleActorPacket attackerPacket = BattlePackets.from (attacker, playerModel, countryMapGraphModel);
    final BattleActorPacket defenderPacket = BattlePackets.from (defender, playerModel, countryMapGraphModel);

    log.info ("Processing battle: Attacker: [{}] | Defender: [{}]", attackerPacket, defenderPacket);

    final int initialAttackerArmyCount = countryArmyModel.getArmyCountFor (attacker.getCountryId ());
    final int initialDefenderAmryCount = countryArmyModel.getArmyCountFor (defender.getCountryId ());

    final AttackOrder attackOrder = turnDataCache.get (CacheKey.BATTLE_PENDING_ATTACK_ORDER, AttackOrder.class);
    final BattleResult result = battleModel.generateResultFor (attackOrder, defender.getDieCount (), playerModel,
                                                               playMapModel);
    log.trace ("Battle result: {}", result);

    // -- send notification and/or occupation request events -- //
    final int newAttackerArmyCount = countryArmyModel.getArmyCountFor (result.getAttacker ().getCountryId ());
    final int newDefenderArmyCount = countryArmyModel.getArmyCountFor (result.getDefender ().getCountryId ());
    final int attackerArmyCountDelta = newAttackerArmyCount - initialAttackerArmyCount;
    final int defenderArmyCountDelta = newDefenderArmyCount - initialDefenderAmryCount;
    final CountryPacket attackerCountry = countryMapGraphModel.countryPacketWith (attacker.getCountryId ());
    final CountryPacket defenderCountry = countryMapGraphModel.countryPacketWith (defender.getCountryId ());
    // publish notification events
    if (attackerArmyCountDelta != 0)
    {
      eventBus.publish (new CountryArmiesChangedEvent (attackerCountry, attackerArmyCountDelta));
    }
    if (defenderArmyCountDelta != 0)
    {
      eventBus.publish (new CountryArmiesChangedEvent (defenderCountry, defenderArmyCountDelta));
    }
    if (result.getDefendingCountryOwner ().isNot (defender.getPlayerId ()))
    {
      final Id newOwnerId = countryOwnerModel.ownerOf (attacker.getCountryId ());
      final Id prevOwnerId = countryOwnerModel.ownerOf (defender.getCountryId ());
      final PlayerPacket prevOwner = playerModel.playerPacketWith (prevOwnerId);
      final PlayerPacket newOwner = playerModel.playerPacketWith (newOwnerId);
      final CountryPacket updatedDefenderCountry = countryMapGraphModel.countryPacketWith (defender.getCountryId ());
      eventBus.publish (new CountryOwnerChangedEvent (updatedDefenderCountry, prevOwner, newOwner));

      // publish occupation request event (this must occur before attack success event in order for the
      // correct state transition to occur)
      eventBus.publish (new PlayerOccupyCountryRequestEvent (newOwner,
              countryMapGraphModel.countryPacketWith (attacker.getCountryId ()), updatedDefenderCountry));
    }

    clearCacheValues (CacheKey.BATTLE_ATTACKER_DATA, CacheKey.BATTLE_DEFENDER_DATA,
                      CacheKey.BATTLE_PENDING_ATTACK_ORDER);

    turnDataCache.put (CacheKey.OCCUPY_SOURCE_COUNTRY, attackerCountry);
    turnDataCache.put (CacheKey.OCCUPY_DEST_COUNTRY, defenderCountry);
    turnDataCache.put (CacheKey.OCCUPY_MIN_ARMY_COUNT, rules.getMinOccupyArmyCount (attackOrder.getDieCount ()));

    final BattleResultPacket resultPacket = BattlePackets.from (result, playerModel, countryMapGraphModel);
    eventBus.publish (new PlayerAttackCountryResponseSuccessEvent (resultPacket));
  }

  @StateMachineCondition
  public boolean verifyPlayerOccupyCountryResponseRequest (final PlayerOccupyCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    checkCacheValues (CacheKey.OCCUPY_SOURCE_COUNTRY, CacheKey.OCCUPY_DEST_COUNTRY, CacheKey.OCCUPY_MIN_ARMY_COUNT);

    final PlayerPacket player = getCurrentPlayerPacket ();

    final CountryPacket sourceCountry = turnDataCache.get (CacheKey.OCCUPY_SOURCE_COUNTRY, CountryPacket.class);
    final CountryPacket destCountry = turnDataCache.get (CacheKey.OCCUPY_DEST_COUNTRY, CountryPacket.class);
    final int minDeltaArmyCount = turnDataCache.get (CacheKey.OCCUPY_MIN_ARMY_COUNT, int.class);
    final int deltaArmyCount = event.getDeltaArmyCount ();

    if (deltaArmyCount < minDeltaArmyCount)
    {
      eventBus.publish (new PlayerOccupyCountryResponseDeniedEvent (player,
              PlayerOccupyCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_BELOW_MIN));
      eventBus.publish (new PlayerOccupyCountryRequestEvent (player, sourceCountry, destCountry));
      return false;
    }

    if (deltaArmyCount > rules.getMaxOccupyArmyCount (sourceCountry.getArmyCount ()))
    {
      eventBus.publish (new PlayerOccupyCountryResponseDeniedEvent (player,
              PlayerOccupyCountryResponseDeniedEvent.Reason.DELTA_ARMY_COUNT_EXCEEDS_MAX));
      eventBus.publish (new PlayerOccupyCountryRequestEvent (player, sourceCountry, destCountry));
      return false;
    }

    final Id sourceCountryId = countryMapGraphModel.countryWith (sourceCountry.getName ());
    final Id destCountryId = countryMapGraphModel.countryWith (destCountry.getName ());
    countryArmyModel.requestToRemoveArmiesFromCountry (sourceCountryId, deltaArmyCount);
    countryArmyModel.requestToAddArmiesToCountry (destCountryId, deltaArmyCount);

    final CountryPacket updatedSourceCountry = countryMapGraphModel.countryPacketWith (sourceCountryId);
    final CountryPacket updatedDestCountry = countryMapGraphModel.countryPacketWith (destCountryId);
    eventBus.publish (new CountryArmiesChangedEvent (updatedSourceCountry, -deltaArmyCount));
    eventBus.publish (new CountryArmiesChangedEvent (updatedDestCountry, deltaArmyCount));
    eventBus.publish (new PlayerOccupyCountryResponseSuccessEvent (player, updatedSourceCountry, updatedDestCountry,
            deltaArmyCount));

    clearCacheValues (CacheKey.OCCUPY_SOURCE_COUNTRY, CacheKey.OCCUPY_DEST_COUNTRY, CacheKey.OCCUPY_MIN_ARMY_COUNT);

    return true;
  }

  @StateMachineAction
  @StateEntryAction
  public void beginFortifyPhase ()
  {
    final PlayerPacket currentPlayerPacket = getCurrentPlayerPacket ();

    log.info ("Begin fortify phase for player [{}].", currentPlayerPacket);

    eventBus.publish (new BeginFortifyPhaseEvent (currentPlayerPacket));

    final Id currentPlayerId = getCurrentPlayerId ();
    final ImmutableSet <CountryPacket> ownedCountries = countryOwnerModel.getCountriesOwnedBy (currentPlayerId);
    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> validFortifyVectors = ImmutableSetMultimap
            .builder ();
    for (final CountryPacket country : ownedCountries)
    {
      if (!country.hasAtLeastNArmies (rules.getMinArmiesOnCountryForAttack ())) continue;
      final Id countryId = countryMapGraphModel.countryWith (country.getName ());
      final ImmutableSet <Id> adjCountries = countryMapGraphModel.getAdjacentNodes (countryId);
      for (final Id adjCountry : adjCountries)
      {
        if (!countryOwnerModel.isCountryOwnedBy (adjCountry, currentPlayerId)) continue;
        validFortifyVectors.put (country, countryMapGraphModel.countryPacketWith (adjCountry));
      }
    }

    eventBus.publish (new PlayerFortifyCountryRequestEvent (currentPlayerPacket, validFortifyVectors.build ()));
  }

  @StateMachineCondition
  public boolean verifyPlayerFortifyCountryResponseRequest (final PlayerFortifyCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Id currentPlayerId = getCurrentPlayerId ();
    final PlayerPacket currentPlayerPacket = getCurrentPlayerPacket ();

    if (!event.isCountryDataPresent ())
    {
      // empty fortify actions do not need to be checked
      eventBus.publish (new PlayerFortifyCountryResponseSuccessEvent (currentPlayerPacket));
      return true;
    }

    if (!countryMapGraphModel.existsCountryWith (event.getSourceCountry ().get ()))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.SOURCE_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    if (!countryMapGraphModel.existsCountryWith (event.getTargetCountry ().get ()))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.TARGET_COUNTRY_DOES_NOT_EXIST));
      return false;
    }

    final Id sourceCountryId = countryMapGraphModel.countryWith (event.getSourceCountry ().get ());
    final Id targetCountryId = countryMapGraphModel.countryWith (event.getTargetCountry ().get ());

    if (!countryOwnerModel.isCountryOwnedBy (sourceCountryId, currentPlayerId))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY));
      return false;
    }

    if (!countryOwnerModel.isCountryOwnedBy (targetCountryId, currentPlayerId))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY));
      return false;
    }

    if (!countryMapGraphModel.areAdjacent (sourceCountryId, targetCountryId))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT));
      return false;
    }

    final int fortifyArmyCount = event.getFortifyArmyCount ();

    if (fortifyArmyCount == 0)
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.FORTIFY_ARMY_COUNT_UNDERFLOW));
      return false;
    }

    if (fortifyArmyCount > rules.getMaxFortifyArmyCount (countryArmyModel.getArmyCountFor (sourceCountryId)))
    {
      eventBus.publish (new PlayerFortifyCountryResponseDeniedEvent (currentPlayerPacket,
              PlayerFortifyCountryResponseDeniedEvent.Reason.FORTIFY_ARMY_COUNT_OVERFLOW));
      return false;
    }

    eventBus.publish (new PlayerFortifyCountryResponseSuccessEvent (currentPlayerPacket,
            countryMapGraphModel.countryPacketWith (sourceCountryId),
            countryMapGraphModel.countryPacketWith (targetCountryId), fortifyArmyCount));

    return true;
  }

  @StateMachineCondition
  public boolean isFull ()
  {
    return playerModel.isFull ();
  }

  @StateMachineCondition
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

  public boolean playerCountIsNot (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIsNot (count);
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

  public PlayerTurnOrder getTurn ()
  {
    return playerTurnModel.getTurnOrder ();
  }

  public boolean turnIs (final PlayerTurnOrder turn)
  {
    Arguments.checkIsNotNull (turn, "turn");

    return playerTurnModel.getTurnOrder ().is (turn);
  }

  public boolean playerLimitIsAtLeast (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIsAtLeast (limit);
  }

  public PlayerPacket getCurrentPlayerPacket ()
  {
    return playerModel.playerPacketWith (playerTurnModel.getTurnOrder ());
  }

  public Id getCurrentPlayerId ()
  {
    return playerModel.playerWith (playerTurnModel.getTurnOrder ());
  }

  public void dumpDataCacheToLog ()
  {
    log.debug ("Turn: {} | Player: [{}] | Cache dump: [{}]", playerTurnModel.getTurn (), getCurrentPlayerId (),
               turnDataCache);
  }

  // checks whether or not a player has won or lost the game in the current game state
  private void checkPlayerGameStatus (final Id playerId)
  {
    final int playerCountryCount = countryOwnerModel.countCountriesOwnedBy (playerId);
    if (playerCountryCount < rules.getMinPlayerCountryCount ())
    {
      eventBus.publish (new PlayerLoseGameEvent (playerModel.playerPacketWith (playerId)));
      playerModel.remove (playerId);
      return;
    }

    if (playerCountryCount >= rules.getWinningCountryCount ())
    {
      // player won! huzzah!
      eventBus.publish (new PlayerWinGameEvent (playerModel.playerPacketWith (playerId)));
      // end the game
      eventBus.publish (new EndGameEvent ());
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

  private static ImmutableMap <CountryPacket, PlayerPacket> buildPlayMapViewFrom (final PlayerModel playerModel,
                                                                                  final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final CountryMapGraphModel countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();

    final ImmutableMap.Builder <CountryPacket, PlayerPacket> playMapView = ImmutableMap.builder ();
    for (final Id countryId : countryMapGraphModel)
    {
      if (!countryOwnerModel.isCountryOwned (countryId)) continue;

      final Id ownerId = countryOwnerModel.ownerOf (countryId);
      playMapView.put (countryMapGraphModel.countryPacketWith (countryId), playerModel.playerPacketWith (ownerId));
    }
    return playMapView.build ();
  }

  public static class Builder
  {
    private final GameRules gameRules;
    private PlayMapModel playMapModel;
    private PlayerModel playerModel;
    private CardModel cardModel;
    private PlayerTurnModel playerTurnModel;
    private BattleModel battleModel;
    private MBassador <Event> eventBus = EventBusFactory.create ();

    public GameModel build ()
    {
      return new GameModel (playerModel, playMapModel, cardModel, playerTurnModel, battleModel, gameRules, eventBus);
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
      final CountryMapGraphModel disjointCountryGraph = CountryMapGraphModel
              .disjointCountryGraphFrom (defaultCountryFactory);
      playMapModel = new DefaultPlayMapModelFactory (gameRules)
              .create (disjointCountryGraph,
                       ContinentMapGraphModel.disjointContinentGraphFrom (emptyContinentFactory, disjointCountryGraph));
      playerModel = new DefaultPlayerModel (gameRules);
      cardModel = new DefaultCardModel (gameRules, ImmutableSet.<Card> of ());
      playerTurnModel = new DefaultPlayerTurnModel (gameRules.getPlayerLimit ());
      battleModel = new DefaultBattleModel (gameRules);
    }
  }
}
