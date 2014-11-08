package com.forerunnergames.peril.core.model.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public enum PlayerColor
{
  BLUE,
  BROWN,
  CYAN,
  GOLD,
  GREEN,
  ORANGE,
  PINK,
  PURPLE,
  RED,
  SILVER,
  UNKNOWN;

  public static int count()
  {
    return values().length;
  }

  public boolean is (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return equals (color);
  }

  public boolean isNot (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return ! is (color);
  }

  public boolean hasNext()
  {
    return (ordinal() < values().length - 1);
  }

  public PlayerColor next()
  {
    if (hasNext())
    {
      return values()[ordinal() + 1];
    }
    else
    {
      throw new IllegalStateException ("Cannot get next " + getClass().getSimpleName() + " value because " + toString() + " is the last value.");
    }
  }

  public String toProperCase()
  {
    return Strings.toProperCase (name());
  }

  public String toLowerCase()
  {
    return name().toLowerCase();
  }

  @Override
  public String toString()
  {
    return name();
  }
}
