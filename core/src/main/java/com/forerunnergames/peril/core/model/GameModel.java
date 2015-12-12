package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.events.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmyChangeDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountriesResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.ContinentPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
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
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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
  private final ContinentMapGraphModel continentMapGraphModel;
  private final CardModel cardModel;
  private final PlayerTurnModel playerTurnModel;
  private final GameRules rules;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel,
                    final PlayMapModel playMapModel,
                    final CardModel cardModel,
                    final PlayerTurnModel playerTurnModel,
                    final GameRules rules,
                    final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.cardModel = cardModel;
    this.playerTurnModel = playerTurnModel;
    this.rules = rules;
    this.eventBus = eventBus;

    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    continentOwnerModel = playMapModel.getContinentOwnerModel ();
    continentMapGraphModel = playMapModel.getContinentMapGraphModel ();
  }

  public static Builder builder (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    return new Builder (rules);
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

  public void beginTurnPhase ()
  {
    log.info ("Turn begins for player [{}].", getCurrentPlayer ().getName ());

    eventBus.publish (new BeginPlayerTurnEvent (getCurrentPlayer ()));
  }

  public void endTurnPhase ()
  {
    log.info ("Turn ends for player [{}].", getCurrentPlayer ().getName ());

    eventBus.publish (new EndPlayerTurnEvent (getCurrentPlayer ()));
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
    final PlayerPacket currentPlayer = getCurrentPlayer ();

    if (countryOwnerModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = buildPlayMapViewFrom (playerModel, playMapModel);
      eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
      return;
    }

    log.info ("Waiting for player [{}] to select a country...", currentPlayer.getName ());
    eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer));
  }

  @StateMachineCondition
  public boolean verifyPlayerCountrySelectionRequest (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final PlayerPacket currentPlayer = getCurrentPlayer ();
    final Id currentPlayerId = playerModel.idOf (currentPlayer.getName ());

    final String selectedCountryName = event.getSelectedCountryName ();

    if (!countryMapGraphModel.existsCountryWith (selectedCountryName))
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (currentPlayer, selectedCountryName,
              PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer));
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
      eventBus.publish (new PlayerSelectCountryRequestEvent (currentPlayer));
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
    final PlayerPacket player = getCurrentPlayer ();
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
    // publish card trade in request
    eventBus.publish (new PlayerReinforceCountriesRequestEvent (player, countryReinforcementBonus,
            continentReinforcementBonus, cardModel.getNextTradeInBonus (), playerOwnedCountries, matchPackets,
            cardCount > rules.getMaxCardsInHand (TurnPhase.REINFORCE)));
    eventBus.publish (new PlayerArmiesChangedEvent (player, totalReinforcementBonus));
    log.info ("Waiting for player [{}] to place reinforcements...", player);
  }

  @StateMachineCondition
  public boolean verifyPlayerCountryReinforcements (final PlayerReinforceCountriesResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final PlayerPacket player = getCurrentPlayer ();
    final Id playerId = playerModel.idOf (player.getName ());

    Result <CountryArmyChangeDeniedEvent.Reason> result = Result.success ();

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
    final PlayerPacket player = getCurrentPlayer ();

    log.info ("Begin attack phase for player [{}].", player);

    eventBus.publish (new BeginAttackPhaseEvent (player));
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

  public boolean playerLimitIsAtLeast (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIsAtLeast (limit);
  }

  public PlayerPacket getCurrentPlayer ()
  {
    return playerModel.playerPacketWith (playerTurnModel.getTurnOrder ());
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
    private MBassador <Event> eventBus = EventBusFactory.create ();

    public GameModel build ()
    {
      return new GameModel (playerModel, playMapModel, cardModel, playerTurnModel, gameRules, eventBus);
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
              .create (defaultCountryFactory, disjointCountryGraph, emptyContinentFactory,
                       ContinentMapGraphModel.disjointContinentGraphFrom (emptyContinentFactory, disjointCountryGraph));
      playerModel = new DefaultPlayerModel (gameRules);
      cardModel = new DefaultCardModel (gameRules, ImmutableSet.<Card> of ());
      playerTurnModel = new DefaultPlayerTurnModel (gameRules.getPlayerLimit ());
    }
  }
}
