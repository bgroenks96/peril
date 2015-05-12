package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public enum PlayerColor implements IterableEnum <PlayerColor>
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

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public PlayerColor next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public PlayerColor previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public PlayerColor first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public PlayerColor last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (final PlayerColor color)
  {
    return IterableEnumHelper.is (this, color);
  }

  @Override
  public boolean isNot (final PlayerColor color)
  {
    return IterableEnumHelper.isNot (this, color);
  }

  @Override
  public int getPosition ()
  {
    return IterableEnumHelper.getPosition (this);
  }

  @Override
  public String toMixedOrdinalPosition ()
  {
    return IterableEnumHelper.toMixedOrdinalPosition (this);
  }

  public static int count ()
  {
    return IterableEnumHelper.count (values ());
  }

  public static ImmutableSet <PlayerColor> validValues ()
  {
    return validValues;
  }

  public boolean hasNextValid ()
  {
    return IterableEnumHelper.hasNextValid (this, values (), validValues);
  }

  public PlayerColor nextValid ()
  {
    return IterableEnumHelper.nextValid (this, values (), validValues);
  }

  public String toLowerCase ()
  {
    return IterableEnumHelper.toLowerCase (this);
  }

  public String toUpperCase ()
  {
    return IterableEnumHelper.toUpperCase (this);
  }

  public String toProperCase ()
  {
    return IterableEnumHelper.toProperCase (this);
  }

  @Override
  public String toString ()
  {
    return name ();
  }
}
