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
  TYPE1 (0b001),
  TYPE2 (0b010),
  TYPE3 (0b100),
  WILDCARD (0b111);

  public static final int VALUE1 = 0b001, VALUE2 = 0b010, VALUE3 = 0b100, VALUE_WILDCARD = 0b111;

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

    if (!valueMap.containsKey (typeValue))
    {
      throw new IllegalArgumentException ("Unrecognized type value [" + typeValue + "].");
    }

    return valueMap.get (typeValue);
  }

  public static boolean isValidValue (final int typeValue)
  {
    return valueMap.containsKey (typeValue);
  }

  public static CardType random ()
  {
    return Randomness.getRandomElementFrom (CardType.values ());
  }

  public int getTypeValue ()
  {
    return typeValue;
  }

  public boolean matches (final CardType otherCardType)
  {
    return (typeValue & otherCardType.getTypeValue ()) != 0;
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
