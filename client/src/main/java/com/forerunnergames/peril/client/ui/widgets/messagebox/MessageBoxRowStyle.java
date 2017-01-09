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

package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.forerunnergames.peril.client.ui.widgets.padding.HorizontalPadding;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class MessageBoxRowStyle
{
  private final String labelStyle;
  private final int labelAlignment;
  private final int height;
  private final HorizontalPadding hPadding = new HorizontalPadding ();

  public MessageBoxRowStyle (final String labelStyle,
                             final int labelAlignment,
                             final int height,
                             final HorizontalPadding hPadding)
  {
    Arguments.checkIsNotNull (labelStyle, "labelStyle");
    Arguments.checkIsNotNegative (labelAlignment, "labelAlignment");
    Arguments.checkIsNotNegative (height, "height");
    Arguments.checkIsNotNull (hPadding, "hPadding");

    this.labelStyle = labelStyle;
    this.labelAlignment = labelAlignment;
    this.height = height;
    this.hPadding.set (hPadding);
  }

  public String getLabelStyle ()
  {
    return labelStyle;
  }

  public int getLabelAlignment ()
  {
    return labelAlignment;
  }

  public int getHeight ()
  {
    return height;
  }

  public int getPaddingLeft ()
  {
    return hPadding.getLeft ();
  }

  public int getPaddingRight ()
  {
    return hPadding.getRight ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Label Style: [{}] | Label Alignment: [{}] | Row Height: [{}] | H-Padding: [{}]",
                           getClass ().getSimpleName (), labelStyle, labelAlignment, height, hPadding);
  }
}
