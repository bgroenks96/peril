package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.integration.TestSessionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;

public class TestFactory
{
  public static final String RANDOM_MODE_SESSION_NAME = TestSessionProvider
          .createUniqueNameFrom ("randomCountrySelection");
  public static final String MANUAL_MODE_SESSION_NAME = TestSessionProvider
          .createUniqueNameFrom ("manualCountrySelection");
  private static final Logger log = LoggerFactory.getLogger (TestFactory.class);

  @BeforeTest // run before core functionality tests begin
  public static void initializeSessions ()
  {
    final GameRules randSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.RANDOM).build ();

    final GameRules manSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.MANUAL).build ();

    final DedicatedGameSession rSession = new DedicatedGameSession (randSelRules,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, DedicatedGameSession.DEFAULT_SERVER_PORT);

    final DedicatedGameSession mSession = new DedicatedGameSession (manSelRules,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, DedicatedGameSession.DEFAULT_SERVER_PORT + 1);

    log.trace ("Initializing test session {}", RANDOM_MODE_SESSION_NAME);
    TestSessionProvider.start (RANDOM_MODE_SESSION_NAME, rSession);

    log.trace ("Initializing test session {}", MANUAL_MODE_SESSION_NAME);
    TestSessionProvider.start (MANUAL_MODE_SESSION_NAME, mSession);
  }

  // ---- Test Game Session Start/Stop ---- //

  @AfterTest
  public static void tearDownSessions ()
  {
    TestSessionProvider.end (RANDOM_MODE_SESSION_NAME);
    TestSessionProvider.end (MANUAL_MODE_SESSION_NAME);
  }

  @Factory
  public Object[] initialGamePhaseTest ()
  {
    return new Object [] { new InitialGamePhaseTest (RANDOM_MODE_SESSION_NAME),
            new InitialGamePhaseTest (MANUAL_MODE_SESSION_NAME) };
  }

  // -------------------------------------- //
}
