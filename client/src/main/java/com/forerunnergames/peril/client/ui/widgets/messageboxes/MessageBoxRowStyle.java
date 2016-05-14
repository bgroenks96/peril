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

package com.forerunnergames.peril.client.ui.widgets.messageboxes;

import com.forerunnergames.tools.common.Arguments;

public final class MessageBoxRowStyle
{
  private final String labelStyleName;
  private final int labelAlignment;
  private final float height;
  private final float paddingLeft;
  private final float paddingRight;

  public MessageBoxRowStyle (final String labelStyleName,
                             final int labelAlignment,
                             final float height,
                             final float paddingLeft,
                             final float paddingRight)
  {
    Arguments.checkIsNotNull (labelStyleName, "labelStyleName");
    Arguments.checkIsNotNegative (height, "height");
    Arguments.checkIsNotNegative (paddingLeft, "paddingLeft");
    Arguments.checkIsNotNegative (paddingRight, "paddingRight");

    this.labelStyleName = labelStyleName;
    this.labelAlignment = labelAlignment;
    this.height = height;
    this.paddingLeft = paddingLeft;
    this.paddingRight = paddingRight;
  }

  public String getLabelStyleName ()
  {
    return labelStyleName;
  }

  public int getLabelAlignment ()
  {
    return labelAlignment;
  }

  public float getHeight ()
  {
    return height;
  }

  public float getPaddingLeft ()
  {
    return paddingLeft;
  }

  public float getPaddingRight ()
  {
    return paddingRight;
  }
}
