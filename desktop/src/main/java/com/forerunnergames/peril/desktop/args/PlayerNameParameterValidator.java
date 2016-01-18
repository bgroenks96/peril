package com.forerunnergames.peril.desktop.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Strings;

public final class PlayerNameParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value) throws ParameterException
  {
    if (!GameSettings.isValidPlayerNameWithoutClanTag (value))
    {
      throw new ParameterException (
              Strings.format ("Invalid value \"{}\" for parameter \"{}\".\n\nValid player name rules:\n\n{}", value, name,
                              GameSettings.VALID_PLAYER_NAME_DESCRIPTION));

    }
  }
}
