package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Strings;

public final class ServerTitleParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!NetworkSettings.isValidServerName (value))
    {
      throw new ParameterException (Strings.format ("Invalid value \"{}\" for parameter \"{}\".", value, name));
    }
  }
}
