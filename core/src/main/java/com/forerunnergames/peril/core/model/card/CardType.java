package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.tools.common.Randomness;

import java.util.HashMap;
import java.util.Map;

public enum CardType
{
  TYPE1 (1),
  TYPE2 (2),
  TYPE3 (3),
  WILDCARD (0);

  private final int typeValue;

  private CardType (final int typeValue)
  {
    this.typeValue = typeValue;
  }

  public int getTypeValue ()
  {
    return typeValue;
  }

  @Override
  public String toString ()
  {
    return String.format ("%s=%d", name (), typeValue);
  }

  private static final Map <Integer, CardType> valueMap = new HashMap <> ();

  static
  {
    for (final CardType cardType : values ())
    {
      valueMap.put (cardType.typeValue, cardType);
    }
  }

  public static CardType fromValue (final int typeValue)
  {
    return valueMap.get (typeValue);
  }

  public static CardType random ()
  {
    return fromValue (Randomness.getRandomIntegerFrom (0, values ().length));
  }
}
