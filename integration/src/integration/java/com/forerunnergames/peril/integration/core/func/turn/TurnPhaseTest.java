package com.forerunnergames.peril.integration.core.func.turn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.core.StateMachineTester;
import com.forerunnergames.peril.integration.core.func.ActionResult;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTest;
import com.forerunnergames.tools.common.Arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public final class TurnPhaseTest
{
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTest.class);
  private final String sessionName;
  private DedicatedGameSession session;
  private TurnPhaseController controller;
  private StateMachineTester stateMachineTester;

  TurnPhaseTest (final String sessionName)
  {
    Arguments.checkIsNotNull (sessionName, "sessionName");

    this.sessionName = sessionName;
  }

  @BeforeClass (alwaysRun = true)
  public void initialize ()
  {
    session = (DedicatedGameSession) TestSessions.get (sessionName);
    controller = new TurnPhaseController (session);
    controller.setUpInitialGamePhase (); // fast forward game state
    stateMachineTester = new StateMachineTester (session.getStateMachine (), log);
  }

  @Test (enabled = false)
  public void testBeginReinforcementPhase ()
  {
    final ActionResult result = controller.waitForReinforcementPhaseToBegin ();
    assertFalse (result.hasAnyFailed ());
    assertEquals (session.getTestClientCount (), result.verified ());
    assertFalse (stateMachineTester.checkError ().isPresent ());
  }
}
