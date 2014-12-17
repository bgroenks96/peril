package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

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
  private static MBassador <Event> eventBus;
  private GameModel gameModel;
  private EventBusHandler eventHandler;

  @BeforeClass
  public static void setupClass()
  {
    final IBusConfiguration eventBusConfiguration = new BusConfiguration()
            .addFeature (Feature.SyncPubSub.Default())
            .addFeature (Feature.AsynchronousHandlerInvocation.Default())
            .addFeature (Feature.AsynchronousMessageDispatch.Default());

    eventBus = new MBassador <> (eventBusConfiguration);

    eventBus.addErrorHandler (new IPublicationErrorHandler()
    {
      @Override
      public void handleError (PublicationError error)
      {
        log.error (error.toString(), error.getCause());
      }
    });
  }

  @Before
  public void setup()
  {
    gameModel = new GameModel (new PlayerModel (INITIAL_PLAYER_LIMIT), eventBus);
    eventHandler = new EventBusHandler().subscribe();
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded()
  {
    final String name = "Test Player";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameSuccessEvent.class));
    assertTrue (playerFrom (eventHandler.lastEvent (PlayerJoinGameSuccessEvent.class)).has (name));
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed()
  {
    for (int i = 1; i <= INITIAL_PLAYER_LIMIT; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player " + i));
    }

    final String name = "Test Player X";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.lastEventWasType (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEvent (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEvent (PlayerJoinGameDeniedEvent.class)), is (PlayerJoinGameDeniedEvent.REASON.GAME_IS_FULL));
  }

  @Test
  public void testHandleChangePlayerLimitRequestSucceeded()
  {
    final int delta = -2;

    gameModel.handleChangePlayerLimitRequest (new ChangePlayerLimitRequestEvent (delta));

    assertTrue (eventHandler.lastEventWasType (ChangePlayerLimitSuccessEvent.class));
    assertThat (newPlayerLimitFrom (eventHandler.lastEvent (ChangePlayerLimitSuccessEvent.class)), is (INITIAL_PLAYER_LIMIT + delta));
  }

  @Test
  public void testHandleChangePlayerLimitRequestFailed()
  {
    final int delta = 2;

    gameModel.handleChangePlayerLimitRequest (new ChangePlayerLimitRequestEvent (delta));

    assertTrue (eventHandler.lastEventWasType (ChangePlayerLimitDeniedEvent.class));
    assertThat (deltaFrom (eventHandler.lastEvent (ChangePlayerLimitDeniedEvent.class)), is (delta));
    assertThat (reasonFrom (eventHandler.lastEvent (ChangePlayerLimitDeniedEvent.class)), is (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS));
  }

  @Test
  public void testIsGameFull()
  {
    for (int i = 1; i <= INITIAL_PLAYER_LIMIT; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("Test Player " + i));
    }

    assertTrue (gameModel.isGameFull());
  }

  private final class EventBusHandler
  {
    private Event lastEvent;

    public EventBusHandler subscribe()
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
