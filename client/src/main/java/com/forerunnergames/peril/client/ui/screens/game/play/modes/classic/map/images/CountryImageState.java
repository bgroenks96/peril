package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import java.util.EnumSet;

public enum CountryImageState implements IterableEnum <CountryImageState>
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
  UNOWNED,
  DISABLED,
  HIGHLIGHT;

  private static ImmutableSet <CountryImageState> validValues = ImmutableSet.copyOf (Collections2.filter (EnumSet
          .allOf (CountryImageState.class), new Predicate <CountryImageState> ()
  {
    @Override
    public boolean apply (final CountryImageState state)
    {
      return state.isNot (HIGHLIGHT);
    }
  }));

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public CountryImageState next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public CountryImageState previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public CountryImageState first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public CountryImageState last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (CountryImageState countryImageState)
  {
    return IterableEnumHelper.is (this, countryImageState);
  }

  @Override
  public boolean isNot (CountryImageState countryImageState)
  {
    return IterableEnumHelper.isNot (this, countryImageState);
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

  @Override
  public String toString ()
  {
    return name ();
  }

  public static int count ()
  {
    return IterableEnumHelper.count (values ());
  }

  public static ImmutableSet <CountryImageState> validValues ()
  {
    return validValues;
  }

  public boolean hasNextValid ()
  {
    return IterableEnumHelper.hasNextValid (this, values (), validValues);
  }

  public CountryImageState nextValid ()
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
}
