/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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

  public static final ImmutableSet <PlayerColor> VALID_VALUES = FluentIterable.from (EnumSet.allOf (PlayerColor.class))
          .filter (new Predicate <PlayerColor> ()
          {
            @Override
            public boolean apply (final PlayerColor input)
            {
              return input.isNot (UNKNOWN);
            }
          }).toSet ();

  public static final ImmutableSet <String> VALID_VALUE_NAMES = FluentIterable.from (VALID_VALUES)
          .transform (new Function <PlayerColor, String> ()
          {
            @Override
            public String apply (final PlayerColor input)
            {
              return input.name ();
            }
          }).toSet ();

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
  public boolean is (final PlayerColor e)
  {
    return IterableEnumHelper.is (this, e);
  }

  @Override
  public boolean isNot (final PlayerColor e)
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

  public static boolean isValidValue (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return VALID_VALUES.contains (color);
  }

  public static boolean isValidValue (final String color)
  {
    Arguments.checkIsNotNull (color, "color");

    return VALID_VALUE_NAMES.contains (color.toUpperCase ());
  }

  public boolean hasNextValid ()
  {
    return IterableEnumHelper.hasNextValid (this, values (), VALID_VALUES);
  }

  public PlayerColor nextValid ()
  {
    return IterableEnumHelper.nextValid (this, values (), VALID_VALUES);
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
