package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

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

  public static int count()
  {
    return values().length;
  }

  public static PlayerTurnOrder getNthTurnOrder (final int nthPlayerTurnOrder)
  {
    Arguments.checkLowerExclusiveBound (nthPlayerTurnOrder, 0, "nthPlayerTurnOrder");
    Arguments.checkUpperInclusiveBound (nthPlayerTurnOrder, count() - 1, "nthPlayerTurnOrder");

    return PlayerTurnOrder.values() [nthPlayerTurnOrder];
  }

  public boolean is (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return this.equals (turnOrder);
  }

  public boolean isNot (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    return ! this.equals (turnOrder);
  }

  public boolean hasNext()
  {
    return (ordinal() < values().length - 1);
  }

  public PlayerTurnOrder next()
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

  public int asInt()
  {
    return ordinal();
  }

  @Override
  public String toString()
  {
    return name();
  }

  public String toMixedOrdinal()
  {
    return Strings.toMixedOrdinal (asInt());
  }

  public String toProperCase()
  {
    return Strings.toProperCase (name());
  }

  public String toLowerCase()
  {
    return name().toLowerCase();
  }
}
