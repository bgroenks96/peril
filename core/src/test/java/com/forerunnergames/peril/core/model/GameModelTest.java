package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.reasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModelTest;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.EventBusHandler;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameModelTest
{
  private static MBassador <Event> eventBus;
  private static EventBusHandler eventHandler;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private PlayerTurnModel playerTurnModel;

  @BeforeClass
  public static void setupClass ()
  {
    eventBus = EventBusFactory.create ();
    eventHandler = new EventBusHandler ();
    eventHandler.subscribe (eventBus);
  }

  @Before
  public void setup ()
  {
    final int defaultTestCountryCount = 30;
    gameModel = createGameModelWithTotalCountryCount (defaultTestCountryCount);
    eventHandler.clearEvents ();
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
  }

  @Test
  public void testDistributeInitialArmiesZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.distributeInitialArmies ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (playMapModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesTenPlayersTenCountries ()
  {
    // test case in honor of Aaron on PR 27 ;)
    // can't use 5, though, because 5 < ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT

    gameModel = createGameModelWithTotalCountryCount (10);
    for (int i = 0; i < 10; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player-" + i));
    }
    assertTrue (gameModel.playerCountIs (10));
    assertTrue (playMapModel.countryCountIs (10));

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (playMapModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    gameModel = createGameModelWithTotalCountryCount (ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);

    addMaxPlayers ();

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (playMapModel.allCountriesAreOwned ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testRandomlyAssignPlayerCountriesZeroPlayers ()
  {
    assertTrue (playerModel.isEmpty ());

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (playMapModel.allCountriesAreUnowned ());
  }

  @Test
  public void testWaitForPlayersToSelectInitialCountriesAllUnowned ()
  {
    addMaxPlayers ();

    assertTrue (playMapModel.allCountriesAreUnowned ());

    gameModel.waitForPlayersToSelectInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = Packets.from (playerModel.playerWith (PlayerTurnOrder.FIRST));
    assertTrue (eventHandler.secondToLastEvent (PlayerSelectCountryRequestEvent.class).getPlayer ()
            .is (expectedPlayer));
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

    gameModel.waitForPlayersToSelectInitialCountries ();

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
    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
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
    // verify that GameModel did NOT advance the turn
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.FIRST);
  }

  @Test
  public void testVerifyPlayerCountrySelectionRequestInvalidCountryAlreadyOwned ()
  {
    addMaxPlayers ();

    final Country country = randomCountry ();
    final PlayerSelectCountryResponseRequestEvent responseRequest = new PlayerSelectCountryResponseRequestEvent (
            country.getName ());
    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);
    assertTrue (eventHandler.secondToLastEventWasType (PlayerSelectCountryResponseSuccessEvent.class));
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.SECOND);

    gameModel.verifyPlayerCountrySelectionRequest (responseRequest);
    assertTrue (eventHandler.secondToLastEventWasType (PlayerSelectCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryRequestEvent.class));
    assertTrue (gameModel.getTurn () == PlayerTurnOrder.SECOND);
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed ()
  {
    addMaxPlayers ();

    final String name = "Test Player X";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)),
                is (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "Test Player";

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
    assertTrue (gameModel.playerLimitIs (maxPlayers));

    for (int i = 1; i <= playerLimit; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player " + i));
    }

    assertTrue (gameModel.isFull ());
  }

  private void addSinglePlayer ()
  {
    assertTrue (gameModel.isEmpty ());
    assertTrue (gameModel.isNotFull ());

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player"));

    assertTrue (gameModel.playerCountIs (1));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
  }

  private Country randomCountry ()
  {
    return Randomness.getRandomElementFrom (playMapModel.getCountries ());
  }

  private GameModel createGameModelWithTotalCountryCount (final int totalCountryCount)
  {
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (totalCountryCount).build ();
    playerModel = new DefaultPlayerModel (gameRules);
    playMapModel = new DefaultPlayMapModel (PlayMapModelTest.generateTestCountries (totalCountryCount),
            ImmutableSet.<Continent> of (), gameRules);
    playerTurnModel = new DefaultPlayerTurnModel (playerModel.getPlayerLimit ());

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    return new GameModel (playerModel, playMapModel, playerTurnModel, gameRules, eventBus);
  }
}
