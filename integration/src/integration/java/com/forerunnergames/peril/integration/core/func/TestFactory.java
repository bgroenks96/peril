package com.forerunnergames.peril.integration.core.func;

import com.forerunnergames.peril.core.model.rules.ClassicGameRules;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.peril.core.shared.net.GameServerType;
import com.forerunnergames.peril.integration.TestSessionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;

public class TestFactory
{
  private static final Logger log = LoggerFactory.getLogger (TestFactory.class);
  public static final String RANDOM_MODE_SESSION_NAME = TestSessionProvider
          .createUniqueNameFrom ("randomCountrySelection");
  public static final String MANUAL_MODE_SESSION_NAME = TestSessionProvider
          .createUniqueNameFrom ("manualCountrySelection");

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
    final GameSession rSession = new GameSession (GameServerType.HOST_AND_PLAY, randSelRules,
            GameSession.DEFAULT_SERVER_PORT + 1);
    final GameSession mSession = new GameSession ();
    log.trace ("Initializing test session {}", RANDOM_MODE_SESSION_NAME);
    TestSessionProvider.start (RANDOM_MODE_SESSION_NAME, rSession);
    log.trace ("Initializing test session {}", MANUAL_MODE_SESSION_NAME);
    TestSessionProvider.start (MANUAL_MODE_SESSION_NAME, mSession);
  }

  @AfterTest
  public static void tearDownSessions ()
  {
    TestSessionProvider.end (RANDOM_MODE_SESSION_NAME);
    TestSessionProvider.end (MANUAL_MODE_SESSION_NAME);
  }

  // -------------------------------------- //
}
