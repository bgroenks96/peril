package com.forerunnergames.peril.integration.core.func.turn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.core.StateMachineMonitor;
import com.forerunnergames.peril.integration.core.func.ActionResult;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTest;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class TurnPhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private String sessionName;
  private DedicatedGameSession session;
  private TurnPhaseController controller;
  private StateMachineMonitor stateMachineMonitor;

  @BeforeMethod
  public void generateTestName (final Method method)
  {
    sessionName = TestSessions.createUniqueNameFrom (method.getName ());
  }

  @AfterMethod
  public void tearDownTest ()
  {
    TestSessions.end (sessionName);
  }

  @Test (enabled = false)
  public void testBeginReinforcementPhase ()
  {
    initializeSession ();
    final ActionResult result = controller.waitForReinforcementPhaseToBegin ();
    assertFalse (result.hasAnyFailed ());
    assertEquals (session.getTestClientCount (), result.verified ());
    assertFalse (stateMachineMonitor.checkError ().isPresent ());
  }

  private void initializeSession ()
  {
    final GameRules rules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT).build ();

    final DedicatedGameSession testSession = new DedicatedGameSession (sessionName,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, rules);

    log.trace ("Initializing test session {}", sessionName);
    TestSessions.start (sessionName, testSession);

    controller = new TurnPhaseController (testSession);
    stateMachineMonitor = new StateMachineMonitor (testSession.getStateMachine (), log);
    session = testSession;

    controller.setUpInitialGamePhase ();
  }
}
