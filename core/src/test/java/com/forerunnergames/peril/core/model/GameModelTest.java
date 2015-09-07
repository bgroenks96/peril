package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.common.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.common.net.events.EventFluency.reasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.EventBusHandler;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardModelTest;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModelTest;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;
import org.junit.Test;

public class GameModelTest
{
  private MBassador <Event> eventBus;
  private EventBusHandler eventHandler;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private StateMachineActionHandler stateMachineActionHandler;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private CardModel cardModel;

  @Before
  public void setup ()
  {
    eventBus = EventBusFactory.create (ImmutableSet.of (EventBusHandler.createEventBusFailureHandler ()));
    eventHandler = new EventBusHandler ();
    eventHandler.subscribe (eventBus);
    final int defaultTestCountryCount = 30;
    stateMachineActionHandler = createGameModelWithTotalCountryCount (defaultTestCountryCount);
  }

  @Test
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    stateMachineActionHandler.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderOnePlayer ()
  {
    addSinglePlayer ();

    stateMachineActionHandler.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (stateMachineActionHandler.isEmpty ());

    stateMachineActionHandler.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    stateMachineActionHandler.distributeInitialArmies ();

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
    assertTrue (stateMachineActionHandler.isEmpty ());

    stateMachineActionHandler.distributeInitialArmies ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayers ()
  {
    addMaxPlayers ();

    for (final Player player : playerModel.getPlayers ())
    {
      playerModel.addArmiesToHandOf (player.getId (), initialArmies);
    }

    stateMachineActionHandler.randomlyAssignPlayerCountries ();

    assertFalse (playMapModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesTenPlayersTenCountries ()
  {
    // test case in honor of Aaron on PR 27 ;)
    // can't use 5, though, because 5 < ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT

    stateMachineActionHandler = createGameModelWithTotalCountryCount (10);
    for (int i = 0; i < 10; ++i)
    {
      stateMachineActionHandler.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }
    assertTrue (stateMachineActionHandler.playerCountIs (10));
    assertTrue (playMapModel.countryCountIs (10));

    for (final Player player : playerModel.getPlayers ())
    {
      playerModel.addArmiesToHandOf (player.getId (),
                                     playMapModel.getCountryCount () / stateMachineActionHandler.getPlayerCount ());
    }

    stateMachineActionHandler.randomlyAssignPlayerCountries ();

    assertFalse (playMapModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    stateMachineActionHandler = createGameModelWithTotalCountryCount (ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);

    addMaxPlayers ();

    for (final Player player : playerModel.getPlayers ())
    {
      playerModel.addArmiesToHandOf (player.getId (),
                                     playMapModel.getCountryCount () / stateMachineActionHandler.getPlayerCount ());
    }

    stateMachineActionHandler.randomlyAssignPlayerCountries ();

    assertTrue (playMapModel.allCountriesAreOwned ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesZeroPlayers ()
  {
    assertTrue (playerModel.isEmpty ());

    stateMachineActionHandler.randomlyAssignPlayerCountries ();

    assertTrue (playMapModel.allCountriesAreUnowned ());
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForPlayersToSelectInitialCountriesAllUnowned ()
  {
    addMaxPlayers ();

    assertTrue (playMapModel.allCountriesAreUnowned ());

    stateMachineActionHandler.waitForPlayersToSelectInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = Packets.from (playerModel.playerWith (PlayerTurnOrder.FIRST));
    assertTrue (eventHandler.lastEvent (PlayerSelectCountryRequestEvent.class).getPlayer ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToSelectInitialCountriesAllOwned ()
  {
    addMaxPlayers ();

    final Player testPlayerOwner = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Country nextCountry : playMapModel.getCountries ())
    {
      playMapModel.requestToAssignCountryOwner (idOf (nextCountry), idOf (testPlayerOwner));
    }

    assertTrue (playMapModel.allCountriesAreOwned ());

    stateMachineActionHandler.waitForPlayersToSelectInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestWhenValid ()
  {
    addMaxPlayers ();

    final String randomCountryName = randomCountry ().getName ();

    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            randomCountryName);
    stateMachineActionHandler.verifyPlayerCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    // verify that game model advanced the turn, as expected
    assertTrue (stateMachineActionHandler.getTurn () == PlayerTurnOrder.SECOND);
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryDoesNotExist ()
  {
    addMaxPlayers ();

    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            "Transylvania");
    stateMachineActionHandler.verifyPlayerCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
    // verify that GameModel did NOT advance the turn
    assertTrue (stateMachineActionHandler.getTurn () == PlayerTurnOrder.FIRST);
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryAlreadyOwned ()
  {
    addMaxPlayers ();

    final Country country = randomCountry ();
    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            country.getName ());
    stateMachineActionHandler.verifyPlayerCountrySelectionRequest (responseRequest);
    // should be successful for first player
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (stateMachineActionHandler.getTurn () == PlayerTurnOrder.SECOND);

    stateMachineActionHandler.verifyPlayerCountrySelectionRequest (responseRequest);
    // unsuccessful for second player
    assertTrue (eventHandler.secondToLastEventWasType (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    // should not have received any more PlayerArmiesChangedEvents
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (stateMachineActionHandler.getTurn () == PlayerTurnOrder.SECOND);
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed ()
  {
    addMaxPlayers ();

    final String name = "TestPlayerX";

    stateMachineActionHandler.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)),
                is (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "TestPlayer";

    stateMachineActionHandler.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
    assertEquals (eventHandler.lastEventOfType (PlayerJoinGameSuccessEvent.class).getPlayerName (), name);
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (stateMachineActionHandler.isEmpty ());

    addSinglePlayer ();

    assertFalse (stateMachineActionHandler.isEmpty ());
  }

  @Test
  public void testIsFull ()
  {
    addMaxPlayers ();

    assertTrue (stateMachineActionHandler.isFull ());
  }

  private void verifyPlayerCountryAssignmentCompleteEvent ()
  {
    for (final Country country : playMapModel.getCountries ())
    {
      assertTrue (playMapModel.isCountryOwned (country.getId ()));
      final PlayerCountryAssignmentCompleteEvent event = eventHandler
              .lastEventOfType (PlayerCountryAssignmentCompleteEvent.class);
      final CountryPacket countryPacket = Packets.from (country);
      final Player player = playerModel.playerWith (playMapModel.ownerOf (idOf (country)));
      assertTrue (Packets.playerMatchesPacket (player, event.getOwner (countryPacket)));
    }
  }

  private void addMaxPlayers ()
  {
    assertTrue (stateMachineActionHandler.playerLimitIs (maxPlayers));

    for (int i = 1; i <= playerLimit; ++i)
    {
      stateMachineActionHandler.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }

    assertTrue (stateMachineActionHandler.isFull ());
  }

  private void addSinglePlayer ()
  {
    assertTrue (stateMachineActionHandler.isEmpty ());
    assertTrue (stateMachineActionHandler.isNotFull ());

    stateMachineActionHandler.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer"));

    assertTrue (stateMachineActionHandler.playerCountIs (1));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
  }

  private Country randomCountry ()
  {
    return Randomness.getRandomElementFrom (playMapModel.getCountries ());
  }

  private StateMachineActionHandler createGameModelWithTotalCountryCount (final int totalCountryCount)
  {
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (totalCountryCount).build ();
    playerModel = new DefaultPlayerModel (gameRules);
    playMapModel = new DefaultPlayMapModel (PlayMapModelTest.generateTestCountries (totalCountryCount),
            ImmutableSet.<Continent> of (), gameRules);
    cardModel = new DefaultCardModel (gameRules, CardModelTest.generateTestCards ());

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    return new StateMachineActionHandler (GameModel.builder (gameRules).eventBus (eventBus).playMapModel (playMapModel)
            .playerModel (playerModel).build ());
  }
}
