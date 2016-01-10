package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IStringConverter;

import com.forerunnergames.peril.common.game.GameMode;

public final class GameModeParameterConverter implements IStringConverter <GameMode>
{
  @Override
  public GameMode convert (final String value)
  {
    return GameMode.valueOf (value.toUpperCase ());
  }
}
