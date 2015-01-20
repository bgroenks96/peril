package com.forerunnergames.peril.server.main;

import com.forerunnergames.peril.core.model.rules.GameMode;

import com.beust.jcommander.IStringConverter;

public class GameModeParameterConverter implements IStringConverter <GameMode>
{
  @Override
  public GameMode convert (final String value)
  {
    return GameMode.valueOf (value.toUpperCase());
  }
}
