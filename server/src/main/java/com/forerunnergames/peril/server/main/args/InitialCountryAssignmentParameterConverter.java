package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IStringConverter;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;

public final class InitialCountryAssignmentParameterConverter implements IStringConverter <InitialCountryAssignment>
{
  @Override
  public InitialCountryAssignment convert (final String value)
  {
    return InitialCountryAssignment.valueOf (value.toUpperCase ());
  }
}
