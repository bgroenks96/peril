package com.forerunnergames.peril.common.game;

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

  @Override
  public String toString ()
  {
    return name ().toLowerCase ().replace ("_", "-");
  }
}
