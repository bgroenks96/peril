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

public final class Padding
{
  private final HorizontalPadding hPadding;
  private final VerticalPadding vPadding;

  public Padding ()
  {
    this (0, 0, 0, 0);
  }

  public Padding (final int left, final int right, final int top, final int bottom)
  {
    Arguments.checkIsNotNegative (left, "left");
    Arguments.checkIsNotNegative (right, "right");
    Arguments.checkIsNotNegative (top, "top");
    Arguments.checkIsNotNegative (bottom, "bottom");

    hPadding = new HorizontalPadding (left, right);
    vPadding = new VerticalPadding (top, bottom);
  }

  public Padding (final HorizontalPadding hPadding, final VerticalPadding vPadding)
  {
    Arguments.checkIsNotNull (hPadding, "hPadding");
    Arguments.checkIsNotNull (vPadding, "vPadding");

    this.hPadding = new HorizontalPadding (hPadding);
    this.vPadding = new VerticalPadding (vPadding);
  }

  public Padding (final Padding padding)
  {
    Arguments.checkIsNotNull (padding, "padding");

    hPadding = new HorizontalPadding (padding.getLeft (), padding.getRight ());
    vPadding = new VerticalPadding (padding.getTop (), padding.getBottom ());
  }

  public HorizontalPadding getHorizontal ()
  {
    return hPadding;
  }

  public void setHorizontal (final HorizontalPadding hPadding)
  {
    Arguments.checkIsNotNull (hPadding, "hPadding");

    this.hPadding.set (hPadding);
  }

  public int getLeft ()
  {
    return hPadding.getLeft ();
  }

  public void setLeft (final int left)
  {
    Arguments.checkIsNotNegative (left, "left");

    hPadding.setLeft (left);
  }

  public int getRight ()
  {
    return hPadding.getRight ();
  }

  public void setRight (final int right)
  {
    Arguments.checkIsNotNegative (right, "right");

    hPadding.setRight (right);
  }

  public VerticalPadding getVertical ()
  {
    return vPadding;
  }

  public void setVertical (final VerticalPadding vPadding)
  {
    Arguments.checkIsNotNull (vPadding, "vPadding");

    this.vPadding.set (vPadding.getTop (), vPadding.getBottom ());
  }

  public int getTop ()
  {
    return vPadding.getTop ();
  }

  public void setTop (final int top)
  {
    Arguments.checkIsNotNegative (top, "top");

    vPadding.setTop (top);
  }

  public int getBottom ()
  {
    return vPadding.getBottom ();
  }

  public void setBottom (final int bottom)
  {
    Arguments.checkIsNotNegative (bottom, "bottom");

    vPadding.setBottom (bottom);
  }

  public void set (final int left, final int right, final int top, final int bottom)
  {
    Arguments.checkIsNotNegative (left, "left");
    Arguments.checkIsNotNegative (right, "right");
    Arguments.checkIsNotNegative (top, "top");
    Arguments.checkIsNotNegative (bottom, "bottom");

    hPadding.set (left, right);
    vPadding.set (top, bottom);
  }

  public void set (final HorizontalPadding hPadding, final VerticalPadding vPadding)
  {
    Arguments.checkIsNotNull (hPadding, "hPadding");
    Arguments.checkIsNotNull (vPadding, "vPadding");

    this.hPadding.set (hPadding);
    this.vPadding.set (vPadding);
  }

  public void set (final Padding padding)
  {
    Arguments.checkIsNotNull (padding, "padding");

    hPadding.set (padding.getHorizontal ());
    vPadding.set (padding.getVertical ());
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Horizontal: [{}] | Vertical: [{}]", getClass ().getSimpleName (), hPadding, vPadding);
  }
}
