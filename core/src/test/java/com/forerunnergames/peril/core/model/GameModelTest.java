package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.reasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModelTest;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameModelTest
{
  private static MBassador <Event> eventBus;

  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private EventBusHandler eventHandler;

  @BeforeClass
  public static void setupClass ()
  {
    eventBus = EventBusFactory.create ();
  }

  @Before
  public void setup ()
  {
    final int defaultTestCountryCount = 30;
    gameModel = createGameModelWithTotalCountryCount (defaultTestCountryCount);
    eventHandler = new EventBusHandler ().subscribe ();
  }

  @Test
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.lastEventWasType (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderOnePlayer ()
  {
    addSinglePlayer ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.lastEventWasType (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.lastEventWasType (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.distributeInitialArmies ();

    final ImmutableSet <PlayerPacket> players = eventHandler.lastEvent (DistributeInitialArmiesCompleteEvent.class)
            .getPlayers ();

    for (final PlayerPacket player : players)
    {
      assertTrue (player.hasArmiesInHand (initialArmies));
    }

    assertTrue (eventHandler.lastEventWasType (DistributeInitialArmiesCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.distributeInitialArmies ();

    assertTrue (eventHandler.lastEventWasType (DistributeInitialArmiesCompleteEvent.class));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (playMapModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.lastEventWasType (PlayerCountryAssignmentCompleteEvent.class));
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
    assertTrue (eventHandler.lastEventWasType (PlayerCountryAssignmentCompleteEvent.class));
    assertPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    gameModel = createGameModelWithTotalCountryCount (ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT);

    addMaxPlayers ();

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (playMapModel.allCountriesAreOwned ());
    assertTrue (eventHandler.lastEventWasType (PlayerCountryAssignmentCompleteEvent.class));
    assertPlayerCountryAssignmentCompleteEvent ();
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

    assertTrue (eventHandler.lastEventWasType (PlayerSelectCountryInputRequestEvent.class));
    // TODO: check event contents once finalized
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

    assertTrue (eventHandler.lastEventWasType (PlayerCountryAssignmentCompleteEvent.class));
    assertPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed ()
  {
    addMaxPlayers ();

    final String name = "Test Player X";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEvent (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEvent (PlayerJoinGameDeniedEvent.class)),
                is (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "Test Player";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameSuccessEvent.class));
    assertEquals (eventHandler.lastEvent (PlayerJoinGameSuccessEvent.class).getPlayerName (), name);
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

  private void assertPlayerCountryAssignmentCompleteEvent ()
  {
    for (final Country country : playMapModel.getCountries ())
    {
      final Player player = playerModel.playerWith (playMapModel.getOwnerOf (idOf (country)));
      final PlayerCountryAssignmentCompleteEvent event = eventHandler
              .lastEvent (PlayerCountryAssignmentCompleteEvent.class);
      final CountryPacket countryPacket = Packets.from (country);
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
    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameSuccessEvent.class));
  }

  private GameModel createGameModelWithTotalCountryCount (final int totalCountryCount)
  {
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (totalCountryCount).build ();
    playerModel = new PlayerModel (gameRules);
    playMapModel = new PlayMapModel (PlayMapModelTest.generateTestCountries (totalCountryCount),
            ImmutableSet.<Continent> of (), gameRules);

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    return new GameModel (playerModel, playMapModel, gameRules, eventBus);
  }

  private static final class EventBusHandler
  {
    private Event lastEvent;

    public <T> T lastEvent (final Class <T> type)
    {
      Arguments.checkIsNotNull (type, "type");

      return type.cast (lastEvent);
    }

    public <T> boolean lastEventWasType (final Class <T> type)
    {
      Arguments.checkIsNotNull (type, "type");

      return type.isInstance (lastEvent);
    }

    @Handler
    public void onEvent (final Event event)
    {
      lastEvent = event;
    }

    public EventBusHandler subscribe ()
    {
      eventBus.subscribe (this);

      return this;
    }
  }
}
