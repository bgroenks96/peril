package com.forerunnergames.peril.server.main;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.core.shared.settings.GameSettings;

public final class MapNameParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!GameSettings.isValidMapName (value))
    {
      throw new ParameterException ("Invalid value \"" + value + "\" for parameter \"" + name + "\".");
    }
  }
}
