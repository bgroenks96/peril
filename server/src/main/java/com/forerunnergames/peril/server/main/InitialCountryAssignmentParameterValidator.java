package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public final class InitialCountryAssignmentParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    try
    {
      InitialCountryAssignment.valueOf (value.toUpperCase ());
    }
    catch (final IllegalArgumentException e)
    {
      throw new ParameterException (new RuntimeException ("Invalid value \"" + value + "\" for parameter \"" + name
              + "\".", e));
    }
  }
}
