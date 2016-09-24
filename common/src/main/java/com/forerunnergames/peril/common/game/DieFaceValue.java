/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

import java.util.Comparator;

public enum DieFaceValue implements IterableEnum <DieFaceValue>
{
  ONE (1),
  TWO (2),
  THREE (3),
  FOUR (4),
  FIVE (5),
  SIX (6);

  public static final Comparator <DieFaceValue> DESCENDING_ORDER = new Comparator <DieFaceValue> ()
  {
    @Override
    public int compare (final DieFaceValue o1, final DieFaceValue o2)
    {
      return o2.compareTo (o1);
    }
  };

  private final int value;

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public DieFaceValue next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public DieFaceValue previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public DieFaceValue first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public DieFaceValue last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (final DieFaceValue e)
  {
    return IterableEnumHelper.is (this, e);
  }

  @Override
  public boolean isNot (final DieFaceValue e)
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
    return values ().length;
  }

  public static DieFaceValue valueOf (final int value)
  {
    return IterableEnumHelper.getNthValue (value, values ());
  }

  public int value ()
  {
    return value;
  }

  public String lowerCaseName ()
  {
    return name ().toLowerCase ();
  }

  DieFaceValue (final int value)
  {
    this.value = value;
  }
}
