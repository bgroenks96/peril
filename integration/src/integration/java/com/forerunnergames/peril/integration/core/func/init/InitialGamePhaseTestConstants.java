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
