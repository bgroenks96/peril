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

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;

import java.util.HashMap;
import java.util.Map;

public enum CardType
{
  TYPE1 (1),
  TYPE2 (2),
  TYPE3 (3),
  WILDCARD (0);

  private static final Map <Integer, CardType> valueMap = new HashMap <> ();

  static
  {
    for (final CardType cardType : values ())
    {
      valueMap.put (cardType.typeValue, cardType);
    }
  }

  private final int typeValue;

  public static CardType fromValue (final int typeValue)
  {
    Arguments.checkIsNotNegative (typeValue, "typeValue");

    final CardType type = valueMap.get (typeValue);
    if (type == null) throw new IllegalArgumentException ("Unrecognized type value [" + typeValue + "].");

    return type;
  }

  public static CardType random ()
  {
    return fromValue (Randomness.getRandomIntegerFrom (0, values ().length - 1));
  }

  public int getTypeValue ()
  {
    return typeValue;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Type: {} Value: {}", getClass ().getSimpleName (), name (), typeValue);
  }

  CardType (final int typeValue)
  {
    this.typeValue = typeValue;
  }
}
