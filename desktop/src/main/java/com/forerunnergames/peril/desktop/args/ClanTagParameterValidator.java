package com.forerunnergames.peril.desktop.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Strings;

public final class ClanTagParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!GameSettings.isValidClanName (value))
    {
      throw new ParameterException (
              Strings.format ("Invalid value \"{}\" for parameter \"{}\".\n\nValid clan tag rules:\n\n{}", value, name,
                              GameSettings.VALID_CLAN_NAME_DESCRIPTION));

    }
  }
}
