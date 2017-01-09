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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images;

import com.forerunnergames.tools.common.enums.IterableEnumHelper;

public enum CountrySecondaryImageState implements CountryImageState <CountrySecondaryImageState>
{
  NONE,
  HOVERED,
  CLICKED;

  @Override
  public String getEnumName ()
  {
    return name ();
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
}
