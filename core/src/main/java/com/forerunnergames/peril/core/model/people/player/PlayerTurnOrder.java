package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public enum PlayerTurnOrder
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

  private static ImmutableSet <PlayerTurnOrder> validValues = ImmutableSet.copyOf (Collections2.filter (
                  EnumSet.allOf (PlayerTurnOrder.class), new Predicate <PlayerTurnOrder> ()
                  {
                    @Override
                    public boolean apply (final PlayerTurnOrder turnOrder)
                    {
                      return turnOrder.isNot (UNKNOWN);
                    }
                  }));

  public static int count ()
  {
    return values ().length;
  }

  public static ImmutableSet <PlayerTurnOrder> validValues ()
  {
    return validValues;
  }

  public static PlayerTurnOrder getNthTurnOrder (final int nthPlayerTurnOrder)
  {
    Arguments.checkLowerExclusiveBound (nthPlayerTurnOrder, 0, "nthPlayerTurnOrder");
    Arguments.checkUpperInclusiveBound (nthPlayerTurnOrder, count () - 1, "nthPlayerTurnOrder");

    return PlayerTurnOrder.values ()[nthPlayerTurnOrder];
  }

  public int asInt ()
  {
    return ordinal ();
  }

  public boolean hasNext ()
  {
    return ordinal () < values ().length - 1 && values ()[ordinal () + 1].isNot (UNKNOWN);
  }

  public boolean is (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.equals (turnOrder);
  }

  public boolean isNot (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return !this.equals (turnOrder);
  }

  public PlayerTurnOrder next ()
  {
    if (hasNext ())
    {
      return values ()[ordinal () + 1];
    }
    else
    {
      throw new IllegalStateException ("Cannot get next " + getClass ().getSimpleName () + " value because "
                      + toString () + " is the last value.");
    }
  }

  public String toLowerCase ()
  {
    return name ().toLowerCase ();
  }

  public String toMixedOrdinal ()
  {
    return Strings.toMixedOrdinal (asInt ());
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
}
