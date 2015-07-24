package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.enums.IterableEnumHelper;

public enum CountrySecondaryImageState implements CountryImageState <CountrySecondaryImageState>
{
  NONE,
  HOVERED,
  CLICKED;

  @Override
  public String toLowerCase ()
  {
    return IterableEnumHelper.toLowerCase (this);
  }

  @Override
  public String toUpperCase ()
  {
    return IterableEnumHelper.toUpperCase (this);
  }

  @Override
  public String toProperCase ()
  {
    return IterableEnumHelper.toProperCase (this);
  }

  @Override
  public boolean hasNext ()
  {
    return IterableEnumHelper.hasNext (this, values ());
  }

  @Override
  public CountrySecondaryImageState next ()
  {
    return IterableEnumHelper.next (this, values ());
  }

  @Override
  public boolean hasPrevious ()
  {
    return IterableEnumHelper.hasPrevious (this);
  }

  @Override
  public CountrySecondaryImageState previous ()
  {
    return IterableEnumHelper.previous (this, values ());
  }

  @Override
  public CountrySecondaryImageState first ()
  {
    return IterableEnumHelper.first (values ());
  }

  @Override
  public CountrySecondaryImageState last ()
  {
    return IterableEnumHelper.last (values ());
  }

  @Override
  public boolean is (final CountrySecondaryImageState e)
  {
    return IterableEnumHelper.is (this, e);
  }

  @Override
  public boolean isNot (final CountrySecondaryImageState e)
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

  @Override
  public String toString ()
  {
    return name ();
  }
}
