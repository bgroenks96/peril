package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;

public enum GameMode
{
  CLASSIC,
  PERIL,
  CUSTOM;

  public static int count ()
  {
    return values ().length;
  }

  public boolean is (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return equals (gameMode);
  }

  public boolean isNot (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return !is (gameMode);
  }

  @Override
  public String toString ()
  {
    return name ().toLowerCase ().replace ("_", "-");
  }
}
