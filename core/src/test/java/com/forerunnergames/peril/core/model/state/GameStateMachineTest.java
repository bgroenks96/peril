package com.forerunnergames.peril.core.model.state;

import static org.junit.Assert.fail;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModelTest;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableSet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameStateMachineTest
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachineTest.class);
  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch (1);
  private static final long COUNT_DOWN_LATCH_WAIT_TIME = 5;
  private static final TimeUnit COUNT_DOWN_LATCH_WAIT_TIME_UNIT = TimeUnit.SECONDS;

  private final MBassador <Event> eventBus = EventBusFactory.create ();

  @Test
  public void testInitialGameStates ()
  {
    final GameStateMachine gameStateMachine = createGameStateMachine (InitialCountryAssignment.RANDOM);
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

  private GameStateMachine createGameStateMachine (final InitialCountryAssignment initialCountryAssignment)
  {
    final int testCountryCount = 20;
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (testCountryCount).initialCountryAssignment (initialCountryAssignment).build ();
    final PlayerModel playerModel = new DefaultPlayerModel (rules);
    final PlayMapModel playMapModel = new DefaultPlayMapModel (
            PlayMapModelTest.generateTestCountries (testCountryCount), ImmutableSet.<Continent> of (), rules);
    final PlayerTurnModel playerTurnModel = new DefaultPlayerTurnModel (playerModel.getPlayerLimit ());
    final GameModel gameModel = new GameModel (playerModel, playMapModel, playerTurnModel, rules, eventBus);

    final GameStateMachine gameStateMachine = new GameStateMachine (gameModel, new GameStateMachineListener ()
    {
      @Override
      public void onEnd ()
      {
        COUNT_DOWN_LATCH.countDown ();
      }
    });

    eventBus.subscribe (gameStateMachine);
    return gameStateMachine;
  }

  private static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy",
                                            "Brittany", "Jonathan", "Adam", "Brian");
  }
}
