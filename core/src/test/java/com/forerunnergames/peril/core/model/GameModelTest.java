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
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerReinforceCountriesResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountriesResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountriesResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
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
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameModelTest
{
  private static final Logger log = LoggerFactory.getLogger (GameModelTest.class);
  private final int defaultTestCountryCount = 30;
  private MBassador <Event> eventBus;
  private EventBusHandler eventHandler;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private PlayerModel playerModel;
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
    gameModel = createGameModelWithCountryCount (defaultTestCountryCount);
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
      assertEquals (initialArmies, event.getDeltaArmyCount ());
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

    gameModel = createGameModelWithCountryCount (10);
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
    gameModel = createGameModelWithCountryCount (ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);

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
    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    // verify that game model advanced the turn, as expected
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.SECOND);
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryDoesNotExist ()
  {
    addMaxPlayers ();

    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            "Transylvania");
    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
    // verify that GameModel did NOT advance the turn
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.FIRST);
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryAlreadyOwned ()
  {
    addMaxPlayers ();

    final Id country = randomCountry ();
    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            countryMapGraphModel.nameOf (country));
    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);
    // should be successful for first player
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.SECOND);

    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);
    // unsuccessful for second player
    assertTrue (eventHandler.secondToLastEventWasType (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    // should not have received any more PlayerArmiesChangedEvents
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.SECOND);
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
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getCurrentPlayer ()
            .is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

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

    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCountryReinforcements (response);
    assertLastEventWasNotDeniedEvent ();
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 2));
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

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerReinforceCountriesRequestEvent.class).getMatches ()
            .asList ().get (0);
    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), match);
    gameModel.verifyPlayerCountryReinforcements (response);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 2));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);
  }

  public void testVerifyPlayerCountryReinforcementWithOptionalTradeIn ()
  {
    // min required less one; this will work as long as the required count is > 3
    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE) - 1;

    cardDeck = CardModelTest.generateCards (CardType.TYPE1, numCardsInHand + 1);

    gameModel = createGameModelWithCountryCount (defaultTestCountryCount);

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

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerReinforceCountriesRequestEvent.class).getMatches ()
            .asList ().get (0);
    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), match);
    gameModel.verifyPlayerCountryReinforcements (response);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseSuccessEvent.class));
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

    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), CardPackets.fromCardMatchSet (ImmutableSet.of (testTradeIn)).asList ().get (0));
    gameModel.verifyPlayerCountryReinforcements (response);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountriesResponseDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountriesResponseDeniedEvent.Reason.CARDS_NOT_IN_HAND));
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

    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCountryReinforcements (response);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseDeniedEvent.class));
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

    final PlayerReinforceCountriesResponseRequestEvent response = new PlayerReinforceCountriesResponseRequestEvent (
            reinforcements.build (), new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCountryReinforcements (response);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountriesResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountriesResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountriesResponseDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountriesResponseDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
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

  private GameModel createGameModelWithCountryCount (final int totalCountryCount)
  {
    gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (totalCountryCount).build ();
    playerModel = new DefaultPlayerModel (gameRules);

    final CountryFactory countryFactory = new CountryFactory ();
    for (int i = 0; i < totalCountryCount; i++)
    {
      countryFactory.newCountryWith ("TestCountry-" + i);
    }
    countryMapGraphModel = CountryMapGraphModelTest.createCountryMapGraphModelWith (countryFactory);

    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModelTest
            .createContinentMapGraphModelWith (continentFactory, countryMapGraphModel);
    final PlayMapModel playMapModel = new DefaultPlayMapModelFactory (gameRules)
            .create (countryFactory, countryMapGraphModel, continentFactory, continentMapGraphModel);
    countryOwnerModel = playMapModel.getCountryOwnerModel ();

    cardModel = new DefaultCardModel (gameRules, cardDeck);

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    return GameModel.builder (gameRules).eventBus (eventBus).playMapModel (playMapModel).playerModel (playerModel)
            .cardModel (cardModel).build ();
  }
}
