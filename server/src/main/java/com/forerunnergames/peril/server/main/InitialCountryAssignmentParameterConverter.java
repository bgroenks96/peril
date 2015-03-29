package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;

import com.beust.jcommander.IStringConverter;

public class InitialCountryAssignmentParameterConverter implements IStringConverter <InitialCountryAssignment>
{
  @Override
  public InitialCountryAssignment convert (final String value)
  {
    return InitialCountryAssignment.valueOf (value.toUpperCase ());
  }
}
