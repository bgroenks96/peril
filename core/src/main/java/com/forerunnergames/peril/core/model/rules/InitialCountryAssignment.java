package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.tools.common.Arguments;

public enum InitialCountryAssignment
{
  MANUAL,
  RANDOM;

  public static int count ()
  {
    return values ().length;
  }

  public boolean is (final InitialCountryAssignment assignment)
  {
    Arguments.checkIsNotNull (assignment, "assignment");

    return equals (assignment);
  }

  public boolean isNot (final InitialCountryAssignment assignment)
  {
    Arguments.checkIsNotNull (assignment, "assignment");

    return !is (assignment);
  }
}
