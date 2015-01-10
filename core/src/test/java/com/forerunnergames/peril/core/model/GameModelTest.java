package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.deltaFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.newPlayerLimitFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.reasonFrom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameModelTest
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private static final int INITIAL_PLAYER_LIMIT = GameSettings.MAX_PLAYERS;
  private static final int INITIAL_ARMIES = 5;
  private static MBassador <Event> eventBus;
  private GameModel gameModel;
  private EventBusHandler eventHandler;

  @BeforeClass
  public static void setupClass ()
  {
    final IBusConfiguration eventBusConfiguration = new BusConfiguration ().addFeature (Feature.SyncPubSub.Default ())
            .addFeature (Feature.AsynchronousHandlerInvocation.Default ())
            .addFeature (Feature.AsynchronousMessageDispatch.Default ());

    eventBus = new MBassador <> (eventBusConfiguration);

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
    final GameRules rules = mock (GameRules.class);
    when (rules.calculateInitialArmies (anyInt ())).thenReturn (INITIAL_ARMIES);
    gameModel = new GameModel (new PlayerModel (INITIAL_PLAYER_LIMIT), rules, eventBus);
    eventHandler = new EventBusHandler ().subscribe ();
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "Test Player";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameSuccessEvent.class));
    assertTrue (playerFrom (eventHandler.lastEvent (PlayerJoinGameSuccessEvent.class)).has (name));
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
                is (PlayerJoinGameDeniedEvent.REASON.GAME_IS_FULL));
  }

  @Test
  public void testHandleChangePlayerLimitRequestSucceeded ()
  {
    final int delta = - 2;

    gameModel.handleChangePlayerLimitRequest (new ChangePlayerLimitRequestEvent (delta));

    assertTrue (eventHandler.lastEventWasType (ChangePlayerLimitSuccessEvent.class));
    assertThat (newPlayerLimitFrom (eventHandler.lastEvent (ChangePlayerLimitSuccessEvent.class)),
                is (INITIAL_PLAYER_LIMIT + delta));
  }

  @Test
  public void testHandleChangePlayerLimitRequestFailed ()
  {
    final int delta = 2;

    gameModel.handleChangePlayerLimitRequest (new ChangePlayerLimitRequestEvent (delta));

    assertTrue (eventHandler.lastEventWasType (ChangePlayerLimitDeniedEvent.class));
    assertThat (deltaFrom (eventHandler.lastEvent (ChangePlayerLimitDeniedEvent.class)), is (delta));
    assertThat (reasonFrom (eventHandler.lastEvent (ChangePlayerLimitDeniedEvent.class)),
                is (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
  }

  @Test
  public void testIsFull ()
  {
    final int delta = 2;

    gameModel.handleChangePlayerLimitRequest (new ChangePlayerLimitRequestEvent (delta));

    addMaxPlayers ();

    assertTrue (gameModel.isFull ());
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (gameModel.isEmpty ());

    addSinglePlayer ();

    assertFalse (gameModel.isEmpty ());
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

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
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.lastEventWasType (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.distributeInitialArmies ();

    assertTrue (eventHandler.lastEventWasType (DistributeInitialArmiesCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.distributeInitialArmies ();

    final ImmutableSet <Player> players = eventHandler.lastEvent (DistributeInitialArmiesCompleteEvent.class)
            .getPlayers ();

    for (final Player player : players)
    {
      assertTrue (player.hasArmiesInHand (INITIAL_ARMIES));
    }

    assertTrue (eventHandler.lastEventWasType (DistributeInitialArmiesCompleteEvent.class));
  }

  private void addSinglePlayer ()
  {
    assertTrue (gameModel.isEmpty ());
    assertTrue (gameModel.isNotFull ());

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player"));

    assertTrue (gameModel.playerCountIs (1));
    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameSuccessEvent.class));
  }

  private void addMaxPlayers ()
  {
    for (int i = 1; i <= GameSettings.MAX_PLAYERS; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player " + i));
    }

    assertTrue (gameModel.isFull ());
  }

  private final class EventBusHandler
  {
    private Event lastEvent;

    public EventBusHandler subscribe ()
    {
      eventBus.subscribe (this);

      return this;
    }

    @Handler
    public void onEvent (final Event event)
    {
      lastEvent = event;
    }

    public <T> boolean lastEventWasType (final Class <T> type)
    {
      Arguments.checkIsNotNull (type, "type");

      return type.isInstance (lastEvent);
    }

    public <T> T lastEvent (final Class <T> type)
    {
      Arguments.checkIsNotNull (type, "type");

      return type.cast (lastEvent);
    }
  }
}
