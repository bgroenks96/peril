package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.common.net.events.EventFluency.withPlayerNameFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
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
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
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
  }

  public static Builder builder (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    return new Builder (rules);
  }

  void beginTurn ()
  {
    log.info ("Turn begins for player [{}].", getCurrentPlayer ().getName ());

    eventBus.publish (new BeginPlayerTurnEvent (Packets.from (getCurrentPlayer ())));
  }

  void endTurn ()
  {
    log.info ("Turn ends for player [{}].", getCurrentPlayer ().getName ());

    eventBus.publish (new EndPlayerTurnEvent (Packets.from (getCurrentPlayer ())));
  }

  void determinePlayerTurnOrder ()
  {
    log.info ("Determining player turn order randomly...");

    final ImmutableSet <Player> players = playerModel.getPlayers ();
    final List <Player> shuffledPlayers = Randomness.shuffle (players);
    final Iterator <Player> randomPlayerItr = shuffledPlayers.iterator ();

    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!randomPlayerItr.hasNext ()) break;

      final Player player = randomPlayerItr.next ();
      playerModel.changeTurnOrderOfPlayer (player.getId (), turnOrder);

      log.info ("Set turn order of player [{}] to [{}].", player.getName (), turnOrder);
    }

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent (Packets.fromPlayers (playerModel.getPlayers ())));
  }

  void distributeInitialArmies ()
  {
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    // Create a status message listing which player received how many armies.
    final StringBuilder statusMessageBuilder = new StringBuilder ();
    for (final Player player : playerModel.getTurnOrderedPlayers ())
    {
      playerModel.addArmiesToHandOf (player.getId (), armies);

      eventBus.publish (new PlayerArmiesChangedEvent (Packets.from (player), armies));

      // @formatter:off
      statusMessageBuilder
              .append (player.getName ())
              .append (" received ")
              .append (armies)
              .append (" armies.\n");
      // @formatter:on
    }
    if (statusMessageBuilder.length () > 0) statusMessageBuilder.deleteCharAt (statusMessageBuilder.length () - 1);

    eventBus.publish (new DistributeInitialArmiesCompleteEvent (Packets.fromPlayers (playerModel.getPlayers ())));
  }

  void waitForCountrySelectionToBegin ()
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

  void randomlyAssignPlayerCountries ()
  {
    // if there are no players, just give up now!
    if (playerModel.isEmpty ())
    {
      log.info ("Skipping random country assignment... no players!");
      return;
    }

    final List <Country> countries = Randomness.shuffle (new HashSet <> (playMapModel.getCountries ()));
    final List <Player> players = Randomness.shuffle (playerModel.getPlayers ());
    final ImmutableList <Integer> playerCountryDistribution = rules
            .getInitialPlayerCountryDistribution (players.size ());

    log.info ("Randomly assigning {} countries to {} players...", countries.size (), players.size ());

    final Iterator <Country> countryItr = countries.iterator ();
    for (int i = 0; i < players.size (); ++i)
    {
      final Player nextPlayer = players.get (i);
      final int playerCountryCount = playerCountryDistribution.get (i);

      int assignSuccessCount = 0; // for logging purposes
      for (int count = 0; count < playerCountryCount && countryItr.hasNext (); count++)
      {
        final Country toAssign = countryItr.next ();
        final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
        result = playMapModel.requestToAssignCountryOwner (idOf (toAssign), idOf (nextPlayer));
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", toAssign.getName (), nextPlayer.getName (),
                    failureReasonFrom (result));
        }
        else
        {
          playerModel.removeArmiesFromHandOf (nextPlayer.getId (), 1);
          assignSuccessCount++;
        }
        countryItr.remove ();
      }

      log.info ("Assigned {} countries to [{}].", assignSuccessCount, nextPlayer.getName ());
      eventBus.publish (new PlayerArmiesChangedEvent (Packets.from (nextPlayer), -1 * assignSuccessCount));
    }

    // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = Packets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));

    eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
  }

  void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Player player = PlayerFactory.create (withPlayerNameFrom (event));
    final Result <PlayerJoinGameDeniedEvent.Reason> result;

    result = playerModel.requestToAdd (player);

    if (result.failed ())
    {
      eventBus.publish (new PlayerJoinGameDeniedEvent (nameOf (player), failureReasonFrom (result)));
      return;
    }

    final PlayerPacket playerPacket = Packets.from (player);

    eventBus.publish (new PlayerJoinGameSuccessEvent (playerPacket));
  }

  void handlePlayerLeaveGame (final PlayerLeaveGameEvent event)
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

  void waitForPlayersToSelectInitialCountries ()
  {
    final Player currentPlayer = getCurrentPlayer ();

    if (playMapModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = Packets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));
      eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
      return;
    }

    log.info ("Waiting for player [{}] to select a country...", currentPlayer.getName ());
    eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
  }

  boolean verifyPlayerCountrySelectionRequest (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}]", event);

    final Player currentPlayer = getCurrentPlayer ();

    final String selectedCountryName = event.getSelectedCountryName ();

    if (!playMapModel.existsCountryWith (selectedCountryName))
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (Packets.from (currentPlayer), selectedCountryName,
              PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
      return false;
    }

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    result = playMapModel.requestToAssignCountryOwner (idOf (playMapModel.countryWith (selectedCountryName)),
                                                       idOf (currentPlayer));
    if (result.failed ())
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (Packets.from (currentPlayer), selectedCountryName,
              failureReasonFrom (result)));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
      return false;
    }

    eventBus.publish (new PlayerSelectCountryResponseSuccessEvent (Packets.from (currentPlayer), selectedCountryName));
    eventBus.publish (new PlayerArmiesChangedEvent (Packets.from (currentPlayer), -1));

    playerTurnModel.advance ();

    return true;
  }

  void beginReinforcementPhase ()
  {
    final Player player = getCurrentPlayer ();

    log.info ("Begin reinforcement phase for player [{}].", player);

    eventBus.publish (new BeginReinforcementPhaseEvent (Packets.from (player)));

    // add country reinforcements and publish event
    final int countryReinforcementBonus = rules
            .calculateCountryReinforcements (playMapModel.countCountriesOwnedBy (player.getId ()));
    int continentReinforcementBonus = 0;
    final ImmutableSet <Continent> playerOwnedContinents = playMapModel.getContinentsOwnedBy (player.getId ());
    for (final Continent cont : playerOwnedContinents)
    {
      continentReinforcementBonus += cont.getReinforcementBonus ();
    }
    final int totalReinforcementBonus = countryReinforcementBonus + continentReinforcementBonus;
    player.addArmiesToHand (totalReinforcementBonus);

    final ImmutableSet <CardSet.Match> matches = cardModel.computeMatchesFor (player.getId ());
    final int cardCount = cardModel.countCardsInHand (player.getId ());
    final ImmutableSet <CountryPacket> playerOwnedCountries = Packets
            .fromCountries (playMapModel.getCountriesOwnedBy (player.getId ()));
    final ImmutableSet <CardSetPacket> matchPackets = Packets.fromCardMatchSet (matches);
    // publish card trade in request
    eventBus.publish (new PlayerReinforceCountriesRequestEvent (Packets.from (player), countryReinforcementBonus,
            continentReinforcementBonus, cardModel.getNextTradeInBonus (), playerOwnedCountries, matchPackets,
            cardCount > rules.getMaxCardsInHand (TurnPhase.REINFORCE)));
    eventBus.publish (new PlayerArmiesChangedEvent (Packets.from (player), totalReinforcementBonus));
    log.info ("Waiting for player [{}] to place reinforcements...", player);
  }

  boolean verifyPlayerCountryReinforcements (final PlayerReinforceCountriesResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Player player = getCurrentPlayer ();

    Result <PlayerReinforceCountriesResponseDeniedEvent.Reason> result = Result.success ();

    // --- process card trade-ins --- //

    final CardSetPacket tradeIn = event.getTradeIn ();

    final ImmutableSet <Card> cards = Packets.toCardSet (tradeIn.getCards (), cardModel);
    final CardSet cardSet = new CardSet (rules, cards);
    if (!cardSet.isEmpty () && !cardSet.isMatch ())
    {
      result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.INVALID_CARD_SET);
    }

    final int cardTradeInBonus = cardModel.getNextTradeInBonus ();

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      result = cardModel.requestTradeInCards (player.getId (), cardSet.match (), TurnPhase.REINFORCE);
    }

    if (!cardSet.isEmpty () && result.succeeded ())
    {
      player.addArmiesToHand (cardTradeInBonus);
    }
    else if (result.failed ())
    {
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (Packets.from (player),
              result.getFailureReason ()));
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
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (Packets.from (player),
              PlayerReinforceCountriesResponseDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
      return false;
    }

    for (final String countryName : reinforcedCountries.keySet ())
    {
      if (!playMapModel.existsCountryWith (countryName))
      {
        result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST);
        break;
      }
      final Country country = playMapModel.countryWith (countryName);
      if (!playMapModel.isCountryOwnedBy (country.getId (), player.getId ()))
      {
        result = Result.failure (PlayerReinforceCountriesResponseDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY);
        break;
      }
      final int reinforcementCount = reinforcedCountries.get (countryName);
      country.addArmies (reinforcementCount);
      player.removeArmiesFromHand (reinforcementCount);
    }

    if (result.failed ())
    {
      eventBus.publish (new PlayerReinforceCountriesResponseDeniedEvent (Packets.from (player),
              result.getFailureReason ()));
      return false;
    }

    eventBus.publish (new PlayerReinforceCountriesResponseSuccessEvent (Packets.from (player)));
    eventBus.publish (new PlayerArmiesChangedEvent (Packets.from (player), cardTradeInBonus - totalReinforcementCount));

    return true;
  }

  void beginAttackPhase ()
  {
    final Player player = getCurrentPlayer ();

    log.info ("Begin attack phase for player [{}].", player);

    eventBus.publish (new BeginAttackPhaseEvent (Packets.from (player)));
  }

  PlayerModel getPlayerModel ()
  {
    return playerModel;
  }

  PlayMapModel getPlayMapModel ()
  {
    return playMapModel;
  }

  PlayerTurnModel getPlayerTurnModel ()
  {
    return playerTurnModel;
  }

  GameRules getRules ()
  {
    return rules;
  }

  MBassador <Event> getEventBus ()
  {
    return eventBus;
  }

  boolean isFull ()
  {
    return playerModel.isFull ();
  }

  boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  boolean playerCountIs (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIs (count);
  }

  boolean playerCountIsNot (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIsNot (count);
  }

  boolean playerLimitIs (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIs (limit);
  }

  int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
  }

  PlayerTurnOrder getTurn ()
  {
    return playerTurnModel.getTurnOrder ();
  }

  boolean playerLimitIsAtLeast (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIsAtLeast (limit);
  }

  Player getCurrentPlayer ()
  {
    return playerModel.playerWith (playerTurnModel.getTurnOrder ());
  }

  private ImmutableMap <Country, Player> buildPlayMapViewFrom (final PlayerModel playerModel,
                                                               final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final ImmutableSet <Country> countries = playMapModel.getCountries ();
    final ImmutableMap.Builder <Country, Player> playMapView = ImmutableMap.builder ();
    for (final Country country : countries)
    {
      if (!playMapModel.isCountryOwned (idOf (country))) continue;

      final Id ownerId = playMapModel.ownerOf (idOf (country));
      playMapView.put (country, playerModel.playerWith (ownerId));
    }
    return playMapView.build ();
  }

  public static class Builder
  {
    private final GameRules gameRules;
    private PlayerModel playerModel;
    private PlayMapModel playMapModel;
    private CardModel cardModel;
    private PlayerTurnModel playerTurnModel;
    private MBassador <Event> eventBus = EventBusFactory.create ();

    public GameModel build ()
    {
      return new GameModel (playerModel, playMapModel, cardModel, playerTurnModel, gameRules, eventBus);
    }

    public Builder playerModel (final PlayerModel playerModel)
    {
      Arguments.checkIsNotNull (playerModel, "playerModel");

      this.playerModel = playerModel;
      return this;
    }

    public Builder playMapModel (final PlayMapModel playMapModel)
    {
      Arguments.checkIsNotNull (playMapModel, "playMapModel");

      this.playMapModel = playMapModel;
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
      playerModel = new DefaultPlayerModel (gameRules);
      playMapModel = new DefaultPlayMapModel (DefaultPlayMapModel.generateDefaultCountries (gameRules),
              ImmutableSet.<Continent> of (), gameRules);
      cardModel = new DefaultCardModel (gameRules, ImmutableSet.<Card> of ());
      playerTurnModel = new DefaultPlayerTurnModel (gameRules.getPlayerLimit ());
    }
  }
}
