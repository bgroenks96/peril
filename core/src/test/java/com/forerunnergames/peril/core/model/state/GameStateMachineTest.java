package com.forerunnergames.peril.core.model.state;

import static org.junit.Assert.fail;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.model.strategy.DefaultGameStrategy;
import com.forerunnergames.peril.core.model.strategy.GameStrategy;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

import org.junit.BeforeClass;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateMachineTest
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachineTest.class);
  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch (1);
  private static final long COUNT_DOWN_LATCH_WAIT_TIME = 5;
  private static final TimeUnit COUNT_DOWN_LATCH_WAIT_TIME_UNIT = TimeUnit.SECONDS;
  private static GameStateMachine gameStateMachine;

  @BeforeClass
  public static void setUpClass ()
  {
    final IBusConfiguration eventBusConfiguration = new BusConfiguration ().addFeature (Feature.SyncPubSub.Default ())
        .addFeature (Feature.AsynchronousHandlerInvocation.Default ())
        .addFeature (Feature.AsynchronousMessageDispatch.Default ());

    final MBassador <Event> eventBus = new MBassador <> (eventBusConfiguration);

    eventBus.addErrorHandler (new IPublicationErrorHandler ()
    {
      @Override
      public void handleError (final PublicationError error)
      {
        log.error (error.toString (), error.getCause ());
      }
    });

    final int initialPlayerLimit = GameSettings.MAX_PLAYERS;
    final PlayerModel playerModel = new PlayerModel (initialPlayerLimit);
    final GameStrategy strategy = new DefaultGameStrategy ();
    final GameModel gameModel = new GameModel (playerModel, strategy, eventBus);

    gameStateMachine = new GameStateMachine (gameModel, new GameStateMachineListener ()
    {
      @Override
      public void onEnd ()
      {
        COUNT_DOWN_LATCH.countDown ();
      }
    });

    eventBus.subscribe (gameStateMachine);
  }

  @Test
  public void testAll ()
  {
    // Simulate creating a new game.
    gameStateMachine.onCreateGameEvent (new CreateGameEvent ());

    // Simulate many players attempting to join the game.
    for (int i = 0; i < 50; ++i)
    {
      gameStateMachine.onPlayerJoinGameRequestEvent (new PlayerJoinGameRequestEvent (getRandomPlayerName ()));
    }

    try
    {
      COUNT_DOWN_LATCH.await (COUNT_DOWN_LATCH_WAIT_TIME, COUNT_DOWN_LATCH_WAIT_TIME_UNIT);
    } catch (final InterruptedException e)
    {
      final String errorMessage = "The test was interrupted.";

      log.error (errorMessage, e);

      fail (errorMessage);
    }
  }

  private static String getRandomPlayerName ()
  {
    final String[] names = { "Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy", "Brittany",
        "Jonathan", "Adam", "Brian" };
    final List <String> shuffledNames = Randomness.shuffle (Arrays.asList (names));

    return shuffledNames.get (0);
  }
}
