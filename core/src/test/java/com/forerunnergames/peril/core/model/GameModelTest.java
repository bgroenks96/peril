package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.reasonFrom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameModelTest
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private static MBassador <Event> eventBus;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private EventBusHandler eventHandler;

  @BeforeClass
  public static void setupClass ()
  {
    eventBus = new MBassador<> ();

    eventBus.addErrorHandler (new IPublicationErrorHandler ()
    {
      @Override
      public void handleError (final PublicationError error)
      {
        log.error (error.toString (), error.getCause ());
      }
    });
  }

  @Before
  public void setup ()
  {
    final GameRules gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS).build ();
    final PlayerModel playerModel = new PlayerModel (gameRules);
    final PlayMapModel playMapModel = new PlayMapModel (mockCountries (), gameRules);

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    gameModel = new GameModel (playerModel, playMapModel, gameRules, eventBus);
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
  public void testRandomlyAssignPlayerCountries ()
  {
    addMaxPlayers ();

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (eventHandler.lastEventWasType (PlayerCountryAssignmentCompleteEvent.class));
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
    assertTrue (eventHandler.lastEvent (PlayerJoinGameSuccessEvent.class).getPlayerName ().equals (name));
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

  private ImmutableSet <Country> mockCountries ()
  {
    ImmutableSet.Builder <Country> countrySetBuilder = ImmutableSet.builder ();
    for (int i = 0; i < 20; i++)
    {
      Country mockedCountry = Mockito.mock (Country.class);
      Id mockedCountryId = IdGenerator.generateUniqueId ();
      Mockito.when (mockedCountry.getCountryName ()).thenReturn (new CountryName ("Country-" + i));
      Mockito.when (mockedCountry.getId ()).thenReturn (mockedCountryId);
      countrySetBuilder.add (mockedCountry);
    }
    return countrySetBuilder.build ();
  }

  private final class EventBusHandler
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
