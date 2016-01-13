package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.common.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.common.net.events.EventFluency.reasonFrom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.EventBusHandler;
import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerFortifyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerTradeInCardsResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountriesResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardModelTest;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapStateBuilder;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModelTest;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModelTest;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import java.util.Iterator;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.ArrayUtils;

public class GameModelTest
{
  private static final Logger log = LoggerFactory.getLogger (GameModelTest.class);
  private final int defaultTestCountryCount = 30;
  private final ImmutableList <String> defaultTestCountries = generateTestCountryNames (defaultTestCountryCount);
  private MBassador <Event> eventBus;
  private EventBusHandler eventHandler;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private CountryOwnerModel countryOwnerModel;
  private CountryMapGraphModel countryMapGraphModel;
  private CardModel cardModel;
  private ImmutableSet <Card> cardDeck = CardModelTest.generateTestCards ();
  private GameRules gameRules;

  @Before
  public void setup ()
  {
    eventBus = EventBusFactory.create (ImmutableSet.of (EventBusHandler.createEventBusFailureHandler ()));
    eventHandler = new EventBusHandler ();
    eventHandler.subscribe (eventBus);
    // crate default play map + game model
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    initializeGameModelWith (playMapModel);
    assert gameModel != null;
  }

  @Test
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderOnePlayer ()
  {
    addSinglePlayer ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.distributeInitialArmies ();

    final ImmutableSet <PlayerPacket> players = eventHandler
            .lastEventOfType (DistributeInitialArmiesCompleteEvent.class).getPlayers ();

    for (final PlayerPacket player : players)
    {
      assertTrue (player.hasArmiesInHand (initialArmies));
    }

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, players.size ()));
    for (final PlayerArmiesChangedEvent event : eventHandler.allEventsOfType (PlayerArmiesChangedEvent.class))
    {
      assertEquals (initialArmies, event.getPlayerDeltaArmyCount ());
    }
  }

  @Test
  public void testDistributeInitialArmiesZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.distributeInitialArmies ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayers ()
  {
    addMaxPlayers ();

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, initialArmies);
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesTenPlayersTenCountries ()
  {
    // test case in honor of Aaron on PR 27 ;)
    // can't use 5, though, because 5 < ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT

    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (10)));
    for (int i = 0; i < 10; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }
    assertTrue (gameModel.playerCountIs (10));
    assertTrue (countryMapGraphModel.countryCountIs (10));

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryMapGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    final int countryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (countryCount)));

    addMaxPlayers ();

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryMapGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (countryOwnerModel.allCountriesAreOwned ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesZeroPlayers ()
  {
    assertTrue (playerModel.isEmpty ());

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForPlayersToSelectInitialCountriesAllUnowned ()
  {
    addMaxPlayers ();

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());

    gameModel.waitForPlayersToSelectInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    assertTrue (eventHandler.lastEvent (PlayerSelectCountryRequestEvent.class).getPlayer ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToSelectInitialCountriesAllOwned ()
  {
    addMaxPlayers ();

    final Id testPlayerOwner = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayerOwner);
    }

    assertTrue (countryOwnerModel.allCountriesAreOwned ());

    gameModel.waitForPlayersToSelectInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestWhenValid ()
  {
    addMaxPlayers ();

    final Id randomCountry = randomCountry ();
    final String randomCountryName = countryMapGraphModel.nameOf (randomCountry);

    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            randomCountryName);
    gameModel.verifyPlayerClaimCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryDoesNotExist ()
  {
    addMaxPlayers ();

    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            "Transylvania");
    gameModel.verifyPlayerClaimCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryAlreadyOwned ()
  {
    addMaxPlayers ();

    final Id country = randomCountry ();
    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            countryMapGraphModel.nameOf (country));
    gameModel.verifyPlayerClaimCountrySelectionRequest (responseRequest);
    // should be successful for first player
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));

    gameModel.advanceTurn (); // state machine does this as state exit action

    gameModel.verifyPlayerClaimCountrySelectionRequest (responseRequest);
    // unsuccessful for second player
    assertTrue (eventHandler.secondToLastEventWasType (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    // should not have received any more PlayerArmiesChangedEvents
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testBeginReinforcementPhase ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    gameModel.beginReinforcementPhase ();

    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    assertTrue (eventHandler.wasFiredExactlyOnce (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getPlayer ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsRequestEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerTradeInCardsRequestEvent.class).getPlayer ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesRequestEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountriesRequestEvent.class).getPlayer ()
            .is (testPlayerPacket));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementNoTradeIns ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    for (int i = 0; i < testPlayerPacket.getArmiesInHand (); i++)
    {
      reinforcements.put (countries.next ().getName (), 1);
    }

    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertTrue (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    assertLastEventWasNotDeniedEvent ();
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 3));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementWithRequiredTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    for (int i = 0; i < testPlayerPacket.getArmiesInHand (); i++)
    {
      reinforcements.put (countries.next ().getName (), 1);
    }

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerTradeInCardsRequestEvent.class).getMatches ()
            .asList ().get (0);
    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (match);
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertTrue (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 3));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);
  }

  public void testVerifyPlayerCountryReinforcementWithOptionalTradeIn ()
  {
    // min required less one; this will work as long as the required count is > 3
    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE) - 1;

    cardDeck = CardModelTest.generateCards (CardType.TYPE1, numCardsInHand + 1);

    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    for (int i = 0; i < testPlayerPacket.getArmiesInHand (); i++)
    {
      reinforcements.put (countries.next ().getName (), 1);
    }

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerTradeInCardsRequestEvent.class).getMatches ()
            .asList ().get (0);

    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (match);
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertTrue (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 2));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    // pre-trade in match set so the cards are no longer in the player's hand
    final ImmutableList <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayer).asList ();
    assertFalse (matches.isEmpty ());
    final CardSet.Match testTradeIn = matches.get (0);
    assertTrue (cardModel.requestTradeInCards (testPlayer, testTradeIn, TurnPhase.REINFORCE).isSuccessful ());

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    for (int i = 0; i < testPlayerPacket.getArmiesInHand (); i++)
    {
      reinforcements.put (countries.next ().getName (), 1);
    }

    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (
            CardPackets.fromCardMatchSet (ImmutableSet.of (testTradeIn)).asList ().get (0));
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertTrue (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    assertTrue (eventHandler.wasNeverFired (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerTradeInCardsResponseDeniedEvent.class).getReason ()
            .equals (PlayerTradeInCardsResponseDeniedEvent.Reason.CARDS_NOT_IN_HAND));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidCountry ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id notOwnedCountry = countryMapGraphModel.getCountryIds ().asList ().get (0);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      if (nextCountry.is (notOwnedCountry)) continue;
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    reinforcements.put (countryMapGraphModel.nameOf (notOwnedCountry), playerModel.getArmiesInHand (testPlayer));

    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertFalse (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountriesResponseDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountriesResponseDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInsufficientArmyCount ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    gameModel.beginReinforcementPhase ();

    final ImmutableMap.Builder <String, Integer> reinforcements = ImmutableMap.builder ();
    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    for (int i = 0; i < playerModel.getArmiesInHand (testPlayer) + 1; i++)
    {
      reinforcements.put (countries.next ().getName (), 1);
    }

    final PlayerTradeInCardsResponseRequestEvent tradeInResponse = new PlayerTradeInCardsResponseRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.handlePlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountriesResponseRequestEvent reinforceResponse = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build ());
    assertFalse (gameModel.verifyPlayerCountryReinforcements (reinforceResponse));

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountriesResponseDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountriesResponseDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
  }

  @Test
  public void testBeginFortifyPhase ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.beginFortifyPhase ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginFortifyPhaseEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (BeginFortifyPhaseEvent.class).getPlayer ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryRequestEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (PlayerFortifyCountryRequestEvent.class).getPlayer ());
    final ImmutableMultimap <CountryPacket, CountryPacket> expectedFortifyVectors;
    expectedFortifyVectors = buildCountryMultimapFromIndices (defaultTestCountries, adj (0, 1, 3), adj (1, 0),
                                                              adj (3, 0));
    assertEquals (expectedFortifyVectors,
                  eventHandler.lastEventOfType (PlayerFortifyCountryRequestEvent.class).getValidFortifyVectors ());
  }

  @Test
  public void testVerifyEmptyPlayerFortifyCountryResponseRequest ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent ());

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseDeniedEvent.class));
  }

  @Test
  public void testVerifyValidPlayerFortifyCountryResponseRequest ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (3), countryArmyCount - 1));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseDeniedEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestSourceCountryNotOwned ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent (
            defaultTestCountries.get (2), defaultTestCountries.get (0), countryArmyCount - 1));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseDeniedEvent.class));
    assertEquals (PlayerFortifyCountryResponseDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY,
                  eventHandler.lastEventOfType (PlayerFortifyCountryResponseDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestTargetCountryNotOwned ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (2), countryArmyCount - 1));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseDeniedEvent.class));
    assertEquals (PlayerFortifyCountryResponseDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY,
                  eventHandler.lastEventOfType (PlayerFortifyCountryResponseDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestCountriesNotAdjacent ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent (
            defaultTestCountries.get (1), defaultTestCountries.get (3), countryArmyCount - 1));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseDeniedEvent.class));
    assertEquals (PlayerFortifyCountryResponseDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT,
                  eventHandler.lastEventOfType (PlayerFortifyCountryResponseDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestTooManyArmies ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.turnIs (PlayerTurnOrder.FIRST));
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.verifyPlayerFortifyCountryResponseRequest (new PlayerFortifyCountryResponseRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (1), countryArmyCount));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseDeniedEvent.class));
    assertEquals (PlayerFortifyCountryResponseDeniedEvent.Reason.FORTIFY_ARMY_COUNT_OVERFLOW,
                  eventHandler.lastEventOfType (PlayerFortifyCountryResponseDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseSuccessEvent.class));
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed ()
  {
    addMaxPlayers ();

    final String name = "TestPlayerX";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)),
                is (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "TestPlayer";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
    assertEquals (eventHandler.lastEventOfType (PlayerJoinGameSuccessEvent.class).getPlayerName (), name);
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (gameModel.isEmpty ());

    addSinglePlayer ();

    assertFalse (gameModel.isEmpty ());
  }

  @Test
  public void testIsFull ()
  {
    addMaxPlayers ();

    assertTrue (gameModel.isFull ());
  }

  // --- private test utility methods --- //

  private void verifyPlayerCountryAssignmentCompleteEvent ()
  {
    for (final Id country : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.isCountryOwned (country));
      final PlayerCountryAssignmentCompleteEvent event = eventHandler
              .lastEventOfType (PlayerCountryAssignmentCompleteEvent.class);
      final CountryPacket countryPacket = countryMapGraphModel.countryPacketWith (country);
      final Id player = countryOwnerModel.ownerOf (country);
      assertEquals (playerModel.playerPacketWith (player), event.getOwner (countryPacket));
    }
  }

  private void addMaxPlayers ()
  {
    assertTrue (gameModel.playerLimitIs (maxPlayers));

    for (int i = 1; i <= playerLimit; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }

    assertTrue (gameModel.isFull ());
  }

  private void addSinglePlayer ()
  {
    assertTrue (gameModel.isEmpty ());
    assertTrue (gameModel.isNotFull ());

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer"));

    assertTrue (gameModel.playerCountIs (1));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
  }

  private void assertLastEventWasNotDeniedEvent ()
  {
    if (eventHandler.lastEventWasType (DeniedEvent.class))
    {
      final DeniedEvent <?> event = eventHandler.lastEvent (DeniedEvent.class);
      fail (event.getReason ().toString ());
    }
  }

  private Id randomCountry ()
  {
    return Randomness.getRandomElementFrom (countryMapGraphModel.getCountryIds ());
  }

  private ImmutableList <Id> countryIdsFor (final ImmutableList <String> countryNames,
                                            final ImmutableList <Integer> indices)
  {
    final ImmutableList.Builder <Id> countryIds = ImmutableList.builder ();
    for (final int i : indices)
    {
      countryIds.add (countryMapGraphModel.countryWith (countryNames.get (i)));
    }
    return countryIds.build ();
  }

  private CountryAdjacencyIndices adj (final int... adjArr)
  {
    assert adjArr != null;
    assert adjArr.length >= 1;

    return new CountryAdjacencyIndices (adjArr [0], ArrayUtils.subarray (adjArr, 1, adjArr.length));
  }

  private ImmutableMultimap <CountryPacket, CountryPacket> buildCountryMultimapFromIndices (final ImmutableList <String> countryNameList,
                                                                                            final CountryAdjacencyIndices... adjacencyIndices)
  {
    assert countryNameList != null;
    assert adjacencyIndices != null;

    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> expectedFortifyVectors = ImmutableSetMultimap
            .builder ();
    for (final CountryAdjacencyIndices adjInd : adjacencyIndices)
    {
      final CountryPacket cp0 = countryMapGraphModel.countryPacketWith (countryNameList.get (adjInd.c0));
      for (final int adj : adjInd.adj)
      {
        final CountryPacket cpAdj = countryMapGraphModel.countryPacketWith (countryNameList.get (adj));
        expectedFortifyVectors.put (cp0, cpAdj);
      }
    }
    return expectedFortifyVectors.build ();
  }

  private void initializeGameModelWith (final PlayMapModel playMapModel)
  {
    gameRules = playMapModel.getRules ();
    playerModel = new DefaultPlayerModel (gameRules);
    cardModel = new DefaultCardModel (gameRules, cardDeck);
    countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    this.playMapModel = playMapModel;

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    gameModel = GameModel.builder (gameRules).eventBus (eventBus).playMapModel (playMapModel).playerModel (playerModel)
            .cardModel (cardModel).build ();
  }

  private PlayMapModel createPlayMapModelWithDisjointMapGraph (final ImmutableList <String> countryNames)
  {
    final CountryFactory factory = new CountryFactory ();
    for (final String name : countryNames)
    {
      factory.newCountryWith (name);
    }
    final CountryMapGraphModel countryMapGraphModel = CountryMapGraphModelTest
            .createDisjointCountryMapGraphModelWith (factory);

    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModelTest
            .createContinentMapGraphModelWith (continentFactory, countryMapGraphModel);
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (countryMapGraphModel.size ()).build ();
    return new DefaultPlayMapModelFactory (gameRules).create (countryMapGraphModel, continentMapGraphModel);
  }

  private PlayMapModel createPlayMapModelWithTestMapGraph (final ImmutableList <String> countryNames)
  {
    final CountryMapGraphModel countryMapGraphModel = createDefaultTestCountryMapGraph (countryNames);
    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModelTest
            .createContinentMapGraphModelWith (continentFactory, countryMapGraphModel);
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (countryMapGraphModel.size ()).build ();
    playMapModel = new DefaultPlayMapModelFactory (gameRules).create (countryMapGraphModel, continentMapGraphModel);
    return playMapModel;
  }

  public static CountryMapGraphModel createDefaultTestCountryMapGraph (final ImmutableList <String> countryNames)
  {
    final DefaultGraphModel.Builder <String> countryNameGraphBuilder = DefaultGraphModel.builder ();
    // set every node adjacent to country 0
    for (int i = 1; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (0), countryNames.get (i));
    }
    // set each country 1-4 adjacent to its sequential neighbors
    for (int i = 2; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (i - 1), countryNames.get (i));
    }
    // complete the cycle by setting country 1 adjacent to last country
    countryNameGraphBuilder.setAdjacent (countryNames.get (countryNames.size () - 1), countryNames.get (1));
    final GraphModel <String> countryNameGraph = countryNameGraphBuilder.build ();
    return CountryMapGraphModelTest.createCountryMapGraphModelFrom (countryNameGraph);
  }

  private static ImmutableList <String> generateTestCountryNames (final int totalCountryCount)
  {
    final ImmutableList.Builder <String> countryNames = ImmutableList.builder ();
    for (int i = 0; i < totalCountryCount; i++)
    {
      countryNames.add ("TestCountry-" + i);
    }
    return countryNames.build ();
  }

  private class CountryAdjacencyIndices
  {
    final int c0;
    final int[] adj;

    CountryAdjacencyIndices (final int c0, final int... adj)
    {
      this.c0 = c0;
      this.adj = adj;
    }
  }
}
