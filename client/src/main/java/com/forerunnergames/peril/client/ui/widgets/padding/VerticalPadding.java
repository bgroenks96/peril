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

package com.forerunnergames.peril.client.ui.widgets.padding;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class VerticalPadding
{
  private int top = 0;
  private int bottom = 0;

  public VerticalPadding ()
  {
    this (0, 0);
  }

  public VerticalPadding (final int top, final int bottom)
  {
    Arguments.checkIsNotNegative (top, "top");
    Arguments.checkIsNotNegative (bottom, "bottom");

    this.top = top;
    this.bottom = bottom;
  }

  public VerticalPadding (final VerticalPadding vPadding)
  {
    Arguments.checkIsNotNull (vPadding, "vPadding");

    top = vPadding.getTop ();
    bottom = vPadding.getBottom ();
  }

  public int getTop ()
  {
    return top;
  }

  public void setTop (final int top)
  {
    Arguments.checkIsNotNegative (top, "top");

    this.top = top;
  }

  public int getBottom ()
  {
    return bottom;
  }

  public void setBottom (final int bottom)
  {
    Arguments.checkIsNotNegative (bottom, "bottom");

    this.bottom = bottom;
  }

  public void set (final int top, final int bottom)
  {
    Arguments.checkIsNotNegative (top, "top");
    Arguments.checkIsNotNegative (bottom, "bottom");

    this.top = top;
    this.bottom = bottom;
  }

  public void set (final VerticalPadding vPadding)
  {
    Arguments.checkIsNotNull (vPadding, "vPadding");

    top = vPadding.getTop ();
    bottom = vPadding.getBottom ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Top: [{}] | Bottom: [{}]", getClass ().getSimpleName (), top, bottom);
  }
}
