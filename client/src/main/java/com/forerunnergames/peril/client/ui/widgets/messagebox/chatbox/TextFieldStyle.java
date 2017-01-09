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

package com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox;

import com.forerunnergames.peril.client.ui.widgets.padding.HorizontalPadding;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

public final class TextFieldStyle
{
  private final String styleName;
  private final int height;
  private final int maxChars;
  private final Pattern filter;
  private final HorizontalPadding hPadding = new HorizontalPadding ();

  public TextFieldStyle (final String styleName,
                         final int height,
                         final HorizontalPadding hPadding,
                         final int maxChars,
                         final Pattern filter)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (styleName, "styleName");
    Arguments.checkIsNotNegative (height, "height");
    Arguments.checkIsNotNull (hPadding, "hPadding");
    Arguments.checkIsNotNegative (maxChars, "maxChars");
    Arguments.checkIsNotNull (filter, "filter");

    this.styleName = styleName;
    this.height = height;
    this.hPadding.set (hPadding);
    this.maxChars = maxChars;
    this.filter = filter;
  }

  public String getStyleName ()
  {
    return styleName;
  }

  public int getHeight ()
  {
    return height;
  }

  public int getMaxChars ()
  {
    return maxChars;
  }

  public Pattern getFilter ()
  {
    return filter;
  }

  public HorizontalPadding gethPadding ()
  {
    return hPadding;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Style Name: [{}] | Height: [{}] | H-Padding: [{}] | Max Chars: [{}] | Filter: [{}]",
                           getClass ().getSimpleName (), styleName, height, hPadding, maxChars, filter);
  }
}
