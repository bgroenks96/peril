package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public enum PlayerTurnOrder implements IterableEnum <PlayerTurnOrder>
{
  UNKNOWN,
  FIRST,
  SECOND,
  THIRD,
  FOURTH,
  FIFTH,
  SIXTH,
  SEVENTH,
  EIGHTH,
  NINTH,
  TENTH;

  private static final ImmutableSet <PlayerTurnOrder> validValues = ImmutableSet.copyOf (Collections2.filter (EnumSet
          .allOf (PlayerTurnOrder.class), new Predicate <PlayerTurnOrder> ()
  {
    @Override
    public boolean apply (final PlayerTurnOrder input)
    {
      return input.isNot (UNKNOWN);
    }
  }));

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public PlayerTurnOrder next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public PlayerTurnOrder previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public PlayerTurnOrder first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public PlayerTurnOrder last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (final PlayerTurnOrder e)
  {
    return IterableEnumHelper.is (this, e);
  }

  @Override
  public boolean isNot (final PlayerTurnOrder e)
  {
    return IterableEnumHelper.isNot (this, e);
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

  public static ImmutableSet <PlayerTurnOrder> validValues ()
  {
    return validValues;
  }

  public boolean hasNextValid ()
  {
    return IterableEnumHelper.hasNextValid (this, values (), validValues);
  }

  public PlayerTurnOrder nextValid ()
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

  public static PlayerTurnOrder getNthTurnOrder (final int nthPlayerTurnOrder)
  {
    Arguments.checkLowerExclusiveBound (nthPlayerTurnOrder, 0, "nthPlayerTurnOrder");
    Arguments.checkUpperInclusiveBound (nthPlayerTurnOrder, count () - 1, "nthPlayerTurnOrder");

    return PlayerTurnOrder.values () [nthPlayerTurnOrder];
  }

  public String toMixedOrdinal ()
  {
    return is (UNKNOWN) ? "?" : Strings.toMixedOrdinal (asInt ());
  }

  public int asInt ()
  {
    return ordinal ();
  }

  @Override
  public String toString ()
  {
    return name ();
  }
}
