package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.integration.TestSessions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

@Test (groups = { "core", "server", "functionality" })
public class TestFactory
{
  public static final String RANDOM_MODE_SESSION_NAME = TestSessions.createUniqueNameFrom ("randomCountrySelection");
  public static final String MANUAL_MODE_SESSION_NAME = TestSessions.createUniqueNameFrom ("manualCountrySelection");
  private static final Logger log = LoggerFactory.getLogger (TestFactory.class);

  @Factory
  public Object[] initialGamePhaseTest ()
  {
    return new Object [] { new InitialGamePhaseTest (RANDOM_MODE_SESSION_NAME),
            new InitialGamePhaseTest (MANUAL_MODE_SESSION_NAME) };
  }

  // ---- Test Game Session Start/Stop ---- //

  @BeforeTest // run before core functionality tests begin
  public static void initializeSessions ()
  {
    final GameRules randSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.RANDOM).build ();

    final GameRules manualSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.MANUAL).build ();

    final DedicatedGameSession randSession = new DedicatedGameSession (randSelRules,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS);

    final DedicatedGameSession manualSession = new DedicatedGameSession (manualSelRules,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS);

    log.trace ("Initializing test session {}", RANDOM_MODE_SESSION_NAME);
    TestSessions.start (RANDOM_MODE_SESSION_NAME, randSession);

    log.trace ("Initializing test session {}", MANUAL_MODE_SESSION_NAME);
    TestSessions.start (MANUAL_MODE_SESSION_NAME, manualSession);
  }

  @AfterTest
  public static void tearDownSessions ()
  {
    TestSessions.end (RANDOM_MODE_SESSION_NAME);
    TestSessions.end (MANUAL_MODE_SESSION_NAME);
  }

  // -------------------------------------- //
}
