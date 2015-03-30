package com.forerunnergames.peril.core.model.state;

import static org.junit.Assert.fail;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.shared.application.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;

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

  private static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron",
                                            "Maddy", "Brittany", "Jonathan", "Adam", "Brian");
  }

  @BeforeClass
  public static void setUpClass ()
  {
    final MBassador <Event> eventBus = EventBusFactory.create();
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS).build ();
    final PlayerModel playerModel = new PlayerModel (rules);
    final GameModel gameModel = new GameModel (playerModel, rules, eventBus);

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
    }
    catch (final InterruptedException e)
    {
      final String errorMessage = "The test was interrupted.";

      log.error (errorMessage, e);

      fail (errorMessage);
    }
  }
}
