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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.colors;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.color.RgbaColorComponent;

public abstract class AbstractTerritoryColor <T extends RgbaColorComponent> implements TerritoryColor <T>
{
  private final T colorComponent;

  protected AbstractTerritoryColor (final T colorComponent)
  {
    Arguments.checkIsNotNull (colorComponent, "colorComponent");

    this.colorComponent = colorComponent;
  }

  @Override
  public final T getComponent ()
  {
    return colorComponent;
  }

  @Override
  public int hashCode ()
  {
    return colorComponent.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    final AbstractTerritoryColor that = (AbstractTerritoryColor) obj;

    return colorComponent.equals (that.colorComponent);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Color Component: {}", getClass ().getSimpleName (), colorComponent);
  }
}
