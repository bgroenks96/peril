package com.forerunnergames.peril.integration.core.func.init;

import com.forerunnergames.tools.common.Classes;

public final class InitialGamePhaseTestConstants
{
  // --- XML Test Group Names --- //
  public static final String INITIAL_GAME_PHASE_TEST_GROUP_NAME = "func.init";
  public static final String MANUAL_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME = INITIAL_GAME_PHASE_TEST_GROUP_NAME + ".manual";
  public static final String RANDOM_COUNTRY_ASSIGNMENT_TEST_GROUP_NAME = INITIAL_GAME_PHASE_TEST_GROUP_NAME + ".random";

  // --- XML Parameter Constant Keys/Values --- //
  public static final String COUNTRY_ASSIGNMENT_MODE_PARAM_KEY = "countryAssignmentMode";
  public static final String MANUAL_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE = "manual";
  public static final String RANDOM_COUNTRY_ASSIGNMENT_MODE_PARAM_VALUE = "random";

  private InitialGamePhaseTestConstants ()
  {
    Classes.instantiationNotAllowed ();
  }
}
