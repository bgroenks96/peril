package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.tools.common.Strings;

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
      throw new ParameterException (
              new RuntimeException (Strings.format ("Invalid value \"{}\" for parameter \"{}\".", value, name, e)));
    }
  }
}
