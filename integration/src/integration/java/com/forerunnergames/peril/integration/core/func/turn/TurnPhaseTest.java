/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.integration.core.func.turn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.TestSessions.TestSession;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.StateMachineMonitor;
import com.forerunnergames.peril.integration.core.func.ActionResult;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTest;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class TurnPhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private static final String SINGLETON_PROVIDER = "SingletonDataProvider";
  private final Set <TestSession> sessions = Sets.newConcurrentHashSet ();

  @DataProvider (name = SINGLETON_PROVIDER)
  public Object[][] generateSessionName (final Method method)
  {
    final String fullMethodName = Strings.format ("{}_{}", getClass ().getSimpleName (), method.getName ());
    return new Object [] [] { { TestSessions.createUniqueNameFrom (fullMethodName),
                                LoggerFactory.getLogger (fullMethodName) } };
  }

  @AfterClass
  public void tearDown ()
  {
    for (final TestSession session : sessions)
    {
      TestSessions.end (session);
    }

    sessions.clear ();
  }

  @Test (dataProvider = SINGLETON_PROVIDER)
  public void testBeginReinforcementPhase (final String sessionName, final Logger log)
  {
    final DedicatedGameSession testSession = createNewTestSession (sessionName);
    final TurnPhaseController controller = new TurnPhaseController (testSession);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (testSession.getStateMachine (), log);
    controller.setUpInitialGamePhase ();
    final ActionResult result = controller.waitForReinforcementPhaseToBegin ();
    TestUtil.pause (100);
    assertTrue (stateMachineMonitor.entered ("ReinforcementPhase").atLeastOnce ());
    assertFalse (result.hasAnyFailed ());
    assertEquals (testSession.getTestClientCount (), result.verified ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  @Test (dataProvider = SINGLETON_PROVIDER)
  public void testValidCountryReinforcement (final String sessionName, final Logger log)
  {
    final DedicatedGameSession testSession = createNewTestSession (sessionName);
    final TurnPhaseController controller = new TurnPhaseController (testSession);
    final StateMachineMonitor stateMachineMonitor = new StateMachineMonitor (testSession.getStateMachine (), log);
    controller.setUpInitialGamePhase ();
    controller.performRandomCountryReinforcement ();
    TestUtil.pause (100);
    assertTrue (stateMachineMonitor.entered ("AttackPhase").atLeastOnce ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  private DedicatedGameSession createNewTestSession (final String sessionName)
  {
    final GameRules rules = ClassicGameRules.builder ().maxHumanPlayers ().build ();

    final DedicatedGameSession testSession = new DedicatedGameSession (sessionName,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, rules);

    log.trace ("Initializing test session {}", sessionName);
    TestSessions.start (sessionName, testSession);

    sessions.add (testSession);

    return testSession;
  }
}
