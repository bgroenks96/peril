package com.forerunnergames.peril.integration.core.func.turn;

import com.beust.jcommander.internal.Sets;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestFactory;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;

public final class TurnPhaseTestFactory
{
  private static final String DEFAULT_SESSION_NAME = TestSessions.createUniqueNameFrom ("defaultTurnPhase");
  private static final Set <String> sessionNames = Sets.newHashSet ();
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTestFactory.class);

  @Factory
  public Object[] createTurnPhaseTest ()
  {
    return new Object [] { new TurnPhaseTest (DEFAULT_SESSION_NAME) };
  }

  // ---- Test Game Session Start/Stop ---- //

  @BeforeTest // run before initial game phase tests begin
  public static void initializeSessions ()
  {
    final GameRules defaultRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.RANDOM).build ();

    final DedicatedGameSession defaultGameSession = new DedicatedGameSession (DEFAULT_SESSION_NAME,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, defaultRules);

    log.trace ("Initializing test session {}", DEFAULT_SESSION_NAME);
    TestSessions.start (DEFAULT_SESSION_NAME, defaultGameSession);

    sessionNames.add (DEFAULT_SESSION_NAME);
  }

  @AfterTest
  public static void tearDownSessions ()
  {
    for (final String session : sessionNames)
    {
      TestSessions.end (session);
    }
  }

  // -------------------------------------- //
}
