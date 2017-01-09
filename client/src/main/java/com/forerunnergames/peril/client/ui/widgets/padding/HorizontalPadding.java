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

public final class HorizontalPadding
{
  private int left = 0;
  private int right = 0;

  public HorizontalPadding ()
  {
    this (0, 0);
  }

  public HorizontalPadding (final int left, final int right)
  {
    Arguments.checkIsNotNegative (left, "left");
    Arguments.checkIsNotNegative (right, "right");

    this.left = left;
    this.right = right;
  }

  public HorizontalPadding (final HorizontalPadding hPadding)
  {
    Arguments.checkIsNotNull (hPadding, "hPadding");

    left = hPadding.getLeft ();
    right = hPadding.getRight ();
  }

  public int getLeft ()
  {
    return left;
  }

  public void setLeft (final int left)
  {
    Arguments.checkIsNotNegative (left, "left");

    this.left = left;
  }

  public int getRight ()
  {
    return right;
  }

  public void setRight (final int right)
  {
    Arguments.checkIsNotNegative (right, "right");

    this.right = right;
  }

  public void set (final int left, final int right)
  {
    Arguments.checkIsNotNegative (left, "left");
    Arguments.checkIsNotNegative (right, "right");

    this.left = left;
    this.right = right;
  }

  public void set (final HorizontalPadding hPadding)
  {
    Arguments.checkIsNotNull (hPadding, "hPadding");

    left = hPadding.getLeft ();
    right = hPadding.getRight ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Left: [{}] | Right: [{}]", getClass ().getSimpleName (), left, right);
  }
}
