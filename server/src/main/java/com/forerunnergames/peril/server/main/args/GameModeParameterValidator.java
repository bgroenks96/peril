package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Strings;

public final class GameModeParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    try
    {
      GameMode.valueOf (value.toUpperCase ());
    }
    catch (final IllegalArgumentException e)
    {
      throw new ParameterException (Strings.format ("Invalid value \"{}\" for parameter \"{}\".", value, name), e);
    }
  }
}
