package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public enum PlayerColor
{
  BLUE,
  BROWN,
  CYAN,
  GOLD,
  GREEN,
  PINK,
  PURPLE,
  RED,
  SILVER,
  TEAL,
  UNKNOWN;

  private static ImmutableSet <PlayerColor> validValues = ImmutableSet.copyOf (Collections2.filter (EnumSet
      .allOf (PlayerColor.class), new Predicate <PlayerColor> ()
  {
    @Override
    public boolean apply (final PlayerColor color)
    {
      return color.isNot (UNKNOWN);
    }
  }));

  public static int count ()
  {
    return values ().length;
  }

  public static ImmutableSet <PlayerColor> validValues ()
  {
    return validValues;
  }

  public boolean hasNext ()
  {
    return ordinal () < values ().length - 1;
  }

  public boolean hasNextValid ()
  {
    return hasNext () && values ()[ordinal () + 1].isNot (UNKNOWN);
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

  public PlayerColor next ()
  {
    if (hasNext ())
    {
      return getNextValue ();
    }
    else
    {
      throw new IllegalStateException (getNextValueErrorMessage ());
    }
  }

  public PlayerColor nextValid ()
  {
    if (hasNextValid ())
    {
      return getNextValue ();
    }
    else
    {
      throw new IllegalStateException (getNextValueErrorMessage ());
    }
  }

  public String toLowerCase ()
  {
    return name ().toLowerCase ();
  }

  public String toProperCase ()
  {
    return Strings.toProperCase (name ());
  }

  @Override
  public String toString ()
  {
    return name ();
  }

  private PlayerColor getNextValue ()
  {
    return values ()[ordinal () + 1];
  }

  private String getNextValueErrorMessage ()
  {
    return "Cannot get next " + getClass ().getSimpleName () + " value because " + toString () + " is the last value.";
  }
}
