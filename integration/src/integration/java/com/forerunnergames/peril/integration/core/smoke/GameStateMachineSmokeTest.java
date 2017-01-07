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

import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.fail;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameSuspendedEvent;
import com.forerunnergames.peril.core.events.DefaultEventRegistry;
import com.forerunnergames.peril.core.events.EventRegistry;
import com.forerunnergames.peril.core.model.game.GameModel;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.state.events.CreateGameEvent;
import com.forerunnergames.peril.core.model.state.events.ResumeGameEvent;
import com.forerunnergames.peril.core.model.state.events.SuspendGameEvent;
import com.forerunnergames.peril.integration.core.CoreFactory;
import com.forerunnergames.peril.integration.core.CoreFactory.GameStateMachineConfig;
import com.forerunnergames.peril.integration.core.StateMachineMonitor;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;

import de.matthiasmann.AsyncExecution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class GameStateMachineSmokeTest
{
  private static final Logger log = LoggerFactory.getLogger (GameStateMachineSmokeTest.class);
  private static final int TEST_COUNTRY_COUNT = 20;
  // TODO: handle event bus errors
  private final MBassador <Event> eventBus = EventBusFactory.create ();
  private final ScheduledExecutorService updateExecutor = Executors.newSingleThreadScheduledExecutor ();
  private StateMachineEventHandler gameStateMachine;
  private StateMachineMonitor monitor;
  private GameModel gameModel;

  @BeforeClass
  public void setUp ()
  {
    final GameRules rules = ClassicGameRules.builder ().maxHumanPlayers ().totalCountryCount (TEST_COUNTRY_COUNT)
            .build ();
    final AsyncExecution asyncExecutor = new AsyncExecution ();
    final EventRegistry eventRegistry = new DefaultEventRegistry (eventBus, asyncExecutor);
    final GameModelConfiguration gameModelConfig = GameModelConfiguration.builder (rules).eventBus (eventBus)
            .eventRegistry (eventRegistry).asyncExecutor (asyncExecutor).build ();
    gameModel = GameModel.create (gameModelConfig);
    final GameStateMachineConfig config = CoreFactory.createDefaultConfigurationFrom (gameModel);
    gameStateMachine = CoreFactory.createGameStateMachine (config);
    monitor = new StateMachineMonitor (gameStateMachine, log);
    eventBus.subscribe (gameStateMachine);
    updateExecutor.scheduleAtFixedRate (new Runnable ()
    {

      @Override
      public void run ()
      {
        asyncExecutor.executeQueuedJobs ();
      }

    }, 0, 250, TimeUnit.MILLISECONDS);
  }

  @AfterClass
  public void tearDown ()
  {
    updateExecutor.shutdown ();
  }

  @Test
  public void testCreateGame ()
  {
    eventBus.publish (new CreateGameEvent ());
    monitor.checkCurrentStateIs ("WaitForGameToBegin");
  }

  @Test (dependsOnMethods = "testCreateGame", timeOut = 10000)
  public void testPlayersJoinGame ()
  {
    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ();

    try
    {
      final int initialDelaySeconds = 1;

      executor.schedule (new Runnable ()
      {
        @Override
        public void run ()
        {
          // Simulate many players attempting to join the game.
          for (int i = 0; i < 50; ++i)
          {
            log.trace ("Adding player {}", i);
            eventBus.publish (new HumanPlayerJoinGameRequestEvent (getRandomPlayerName ()));
          }
        }
      }, initialDelaySeconds, TimeUnit.SECONDS);

      final long stateChangeTimeoutMs = 10000;
      monitor.waitForStateChangeWithPrior ("PlayingGame", stateChangeTimeoutMs);
    }
    finally
    {
      executor.shutdown ();
    }
  }

  @Test (dependsOnMethods = "testPlayersJoinGame")
  public void testSuspendGame ()
  {
    final int eventDelay = 1;
    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ();
    try
    {
      final CompletableFuture <String> initialState = new CompletableFuture <> ();
      executor.schedule (new Runnable ()
      {
        @Override
        public void run ()
        {
          initialState.complete (gameStateMachine.getCurrentGameStateName ());
          eventBus.publish (new SuspendGameEvent (GameSuspendedEvent.Reason.REQUESTED_BY_HOST));
        }
      }, eventDelay, TimeUnit.SECONDS);

      final long stateChangeTimeoutMs = 5000;
      monitor.waitForStateChangeWithCurrent ("Suspended", stateChangeTimeoutMs);
      executor.schedule (new Runnable ()
      {
        @Override
        public void run ()
        {
          eventBus.publish (new ResumeGameEvent ());
        }
      }, eventDelay, TimeUnit.SECONDS);

      assertNotEquals ("ResumeGame", initialState.get (), "Initial state should not be 'ResumeGame'!");
      monitor.waitForStateChangeWithCurrent (initialState.get (), stateChangeTimeoutMs);
      log.debug ("State History: [{}]", monitor.dumpStateHistory ());
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace ();
      fail (e.toString ());
    }
    catch (final ExecutionException e)
    {
      e.printStackTrace ();
      fail (e.toString ());
    }
    finally
    {
      executor.shutdown ();
    }
  }

  private static String getRandomPlayerName ()
  {
    return Randomness.getRandomElementFrom ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron", "Maddy",
                                            "Brittany", "Jonathan", "Adam", "Brian");
  }
}
