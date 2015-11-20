package com.forerunnergames.peril.common.game;

import java.util.Comparator;

public enum DieFaceValue
{
  ONE (1),
  TWO (2),
  THREE (3),
  FOUR (4),
  FIVE (5),
  SIX (6);

  public static Comparator <DieFaceValue> DESCENDING_ORDER = new Comparator <DieFaceValue> ()
  {
    @Override
    public int compare (final DieFaceValue o1, final DieFaceValue o2)
    {
      return o2.compareTo (o1);
    }
  };

  private final int value;

  public static int count ()
  {
    return values ().length;
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
