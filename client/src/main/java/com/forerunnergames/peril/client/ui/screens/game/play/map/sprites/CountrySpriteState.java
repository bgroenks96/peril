package com.forerunnergames.peril.client.ui.screens.game.play.map.sprites;

import com.forerunnergames.tools.common.enums.IterableEnum;
import com.forerunnergames.tools.common.enums.IterableEnumHelper;

public enum CountrySpriteState implements IterableEnum <CountrySpriteState>
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
  DISABLED,
  HIGHLIGHT,
  UNOWNED;

  public static int count ()
  {
    return IterableEnumHelper.count (values ());
  }

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public CountrySpriteState next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public CountrySpriteState previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public CountrySpriteState first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public CountrySpriteState last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (CountrySpriteState countrySpriteState)
  {
    return IterableEnumHelper.is (this, countrySpriteState);
  }

  @Override
  public boolean isNot (CountrySpriteState countrySpriteState)
  {
    return IterableEnumHelper.isNot (this, countrySpriteState);
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

  @Override
  public String toString()
  {
    return name ();
  }
}
