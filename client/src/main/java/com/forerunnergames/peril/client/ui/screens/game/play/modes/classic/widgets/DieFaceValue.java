package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import java.util.Comparator;

public enum DieFaceValue
{
  ONE,
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX;

  public static Comparator <DieFaceValue> DESCENDING_ORDER = new Comparator <DieFaceValue> ()
  {
    @Override
    public int compare (final DieFaceValue o1, final DieFaceValue o2)
    {
      return o2.compareTo (o1);
    }
  };

  public static int count ()
  {
    return values ().length;
  }
}
