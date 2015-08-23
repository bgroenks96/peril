package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

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

  public String toProperCase ()
  {
    return Strings.toProperCase (name ());
  }
}
