package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IStringConverter;

import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;

public final class InitialCountryAssignmentParameterConverter implements IStringConverter <InitialCountryAssignment>
{
  @Override
  public InitialCountryAssignment convert (final String value)
  {
    return InitialCountryAssignment.valueOf (value.toUpperCase ());
  }
}
