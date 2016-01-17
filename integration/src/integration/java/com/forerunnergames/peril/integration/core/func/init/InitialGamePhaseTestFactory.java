package com.forerunnergames.peril.integration.core.func.init;

import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.COUNTRY_ASSIGNMENT_MODE_PARAM_KEY;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.INITIAL_GAME_PHASE_TEST_GROUP_NAME;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.MANUAL_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE;
import static com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseTestConstants.RANDOM_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE;

import com.beust.jcommander.internal.Sets;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.integration.TestSessions;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.tools.common.Strings;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Factory;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Test (groups = { INITIAL_GAME_PHASE_TEST_GROUP_NAME })
public class InitialGamePhaseTestFactory
{
  private static final String RANDOM_MODE_SESSION_NAME = TestSessions.createUniqueNameFrom ("randomCountryAssignment");
  private static final String MANUAL_MODE_SESSION_NAME = TestSessions.createUniqueNameFrom ("manualCountryAssignment");
  private static final Set <String> sessionNames = Sets.newHashSet ();
  private static final Logger log = LoggerFactory.getLogger (InitialGamePhaseTestFactory.class);

  @BeforeTest // run before initial game phase tests begin
  public static void initializeSessions ()
  {
    final GameRules randSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.RANDOM).build ();

    final GameRules manualSelRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT)
            .initialCountryAssignment (InitialCountryAssignment.MANUAL).build ();

    final DedicatedGameSession randSession = new DedicatedGameSession (RANDOM_MODE_SESSION_NAME,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, randSelRules);

    final DedicatedGameSession manualSession = new DedicatedGameSession (MANUAL_MODE_SESSION_NAME,
            DedicatedGameSession.FAKE_EXTERNAL_SERVER_ADDRESS, manualSelRules);

    log.trace ("Initializing test session {}", RANDOM_MODE_SESSION_NAME);
    TestSessions.start (RANDOM_MODE_SESSION_NAME, randSession);

    log.trace ("Initializing test session {}", MANUAL_MODE_SESSION_NAME);
    TestSessions.start (MANUAL_MODE_SESSION_NAME, manualSession);

    sessionNames.add (RANDOM_MODE_SESSION_NAME);
    sessionNames.add (MANUAL_MODE_SESSION_NAME);
  }

  // ---- Test Game Session Start/Stop ---- //

  @AfterTest
  public static void tearDownSessions ()
  {
    for (final String session : sessionNames)
    {
      TestSessions.end (session);
    }
  }

  @Factory
  @Parameters ({ COUNTRY_ASSIGNMENT_MODE_PARAM_KEY })
  public Object[] createInitialGamePhaseTestWithMode (final String countryAssignmentMode)
  {
    switch (countryAssignmentMode)
    {
      case RANDOM_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE:
        return new Object [] { new InitialGamePhaseTest (RANDOM_MODE_SESSION_NAME) };
      case MANUAL_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE:
        return new Object [] { new InitialGamePhaseTest (MANUAL_MODE_SESSION_NAME) };
      default:
        throw new IllegalArgumentException (Strings.format ("Illegal parameter value for key {}: {}",
                                                            COUNTRY_ASSIGNMENT_MODE_PARAM_KEY, countryAssignmentMode));
    }
  }

  // -------------------------------------- //
}
