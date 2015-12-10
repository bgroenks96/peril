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

  public static Comparator <DieFaceValue> DESCENDING_ORDER = new Comparator <DieFaceValue> ()
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
