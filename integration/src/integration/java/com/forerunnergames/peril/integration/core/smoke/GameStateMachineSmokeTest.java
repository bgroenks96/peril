package com.forerunnergames.peril.integration.core.smoke;

import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.GameModelBuilder;
import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.GameStateMachine;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.integration.core.StateMachineTest;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GameStateMachineSmokeTest
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachineSmokeTest.class);
  private static final int TEST_COUNTRY_COUNT = 20;
  // TODO: handle event bus errors
  private final MBassador <Event> eventBus = EventBusFactory.create ();
  private GameStateMachine gameStateMachine;
  private GameModel gameModel;

  @BeforeClass
  public void setUp ()
  {
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (TEST_COUNTRY_COUNT).build ();
    final GameModelBuilder builder = new GameModelBuilder (rules);
    builder.eventBus (eventBus);
    gameModel = builder.build ();
    config.setGameModel (gameModel);
    gameStateMachine = CoreFactory.createGameStateMachine (config);
    eventBus.subscribe (gameStateMachine);
  }

  @Test
  public void testCreateGame ()
  {
    final StateMachineTest stateTest = new StateMachineTest (gameStateMachine, log);
    eventBus.publish (new CreateGameEvent ());
    stateTest.checkStateIs ("WaitForGameToBegin");
  }

  @Test (dependsOnMethods = "testCreateGame", timeOut = StateMachineTest.DEFAULT_TEST_TIMEOUT)
  public void testPlayersJoinGame ()
  {
    final StateMachineTest stateTest = new StateMachineTest (gameStateMachine, log);
    // Simulate many players attempting to join the game.
    for (int i = 0; i < 50; ++i)
    {
      log.trace ("Adding player {}", i);
      eventBus.publish (new PlayerJoinGameRequestEvent (getRandomPlayerName ()));
    }
    stateTest.waitForStateChange ("PlayingGame", StateMachineTest.DEFAULT_STATE_CHANGE_TIMOUT);
  }

  private static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy",
                                            "Brittany", "Jonathan", "Adam", "Brian");
  }
}
