package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.net.GameServerType;
import com.forerunnergames.tools.common.Strings;

public final class ServerTypeParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    try
    {
      GameServerType.valueOf (value.replace ('-', '_').toUpperCase ());
    }
    catch (final IllegalArgumentException e)
    {
      throw new ParameterException (Strings.format ("Invalid value \"{}\" for parameter \"{}\".", value, name), e);
    }
  }
}
