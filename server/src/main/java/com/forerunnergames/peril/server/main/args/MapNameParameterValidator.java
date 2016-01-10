package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Strings;

public final class MapNameParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!GameSettings.isValidMapName (value))
    {
      throw new ParameterException (Strings.format ("Invalid value \"{}\" for parameter \"{}\".", value, name));
    }
  }
}
