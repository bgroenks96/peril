/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.integration.core.smoke;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.integration.core.StateMachineTester;
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
  private StateMachineEventHandler gameStateMachine;
  private GameModel gameModel;

  @BeforeClass
  public void setUp ()
  {
    final GameStateMachineConfig config = new GameStateMachineConfig ();
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (TEST_COUNTRY_COUNT).build ();
    final GameModel.Builder builder = GameModel.builder (rules);
    builder.eventBus (eventBus);
    gameModel = builder.build ();
    config.setGameModel (gameModel);
    gameStateMachine = CoreFactory.createGameStateMachine (config);
    eventBus.subscribe (gameStateMachine);
  }

  @Test
  public void testCreateGame ()
  {
    final StateMachineTester stateTest = new StateMachineTester (gameStateMachine, log);
    eventBus.publish (new CreateGameEvent ());
    stateTest.checkCurrentStateIs ("WaitForGameToBegin");
  }

  @Test (dependsOnMethods = "testCreateGame", timeOut = StateMachineTester.DEFAULT_TEST_TIMEOUT)
  public void testPlayersJoinGame ()
  {
    final StateMachineTester stateTest = new StateMachineTester (gameStateMachine, log);
    // Simulate many players attempting to join the game.
    for (int i = 0; i < 50; ++i)
    {
      log.trace ("Adding player {}", i);
      eventBus.publish (new PlayerJoinGameRequestEvent (getRandomPlayerName ()));
    }
    stateTest.waitForStateChange ("PlayingGame", StateMachineTester.DEFAULT_STATE_CHANGE_TIMOUT);
  }

  private static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy",
                                            "Brittany", "Jonathan", "Adam", "Brian");
  }
}
