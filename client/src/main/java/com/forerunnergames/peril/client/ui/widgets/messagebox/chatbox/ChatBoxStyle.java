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

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxStyle;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.util.regex.Pattern;

public final class ChatBoxStyle extends MessageBoxStyle
{
  private final int scrollPaneHeight;
  private final int scrollPaneTextFieldSpacing;
  private final TextFieldStyle textFieldStyle;

  public ChatBoxStyle (final MessageBoxStyle style,
                       final int scrollPaneHeight,
                       final int scrollPaneTextFieldSpacing,
                       final TextFieldStyle textFieldStyle)
  {
    super (style);

    Arguments.checkIsNotNegative (scrollPaneHeight, "scrollPaneHeight");
    Arguments.checkIsNotNegative (scrollPaneTextFieldSpacing, "scrollPaneTextFieldSpacing");
    Arguments.checkIsNotNull (textFieldStyle, "textFieldStyle");

    this.scrollPaneHeight = scrollPaneHeight;
    this.scrollPaneTextFieldSpacing = scrollPaneTextFieldSpacing;
    this.textFieldStyle = textFieldStyle;
  }

  public int getScrollPaneHeight ()
  {
    return scrollPaneHeight;
  }

  public int getScrollPaneTextFieldSpacing ()
  {
    return scrollPaneTextFieldSpacing;
  }

  public String getTextFieldStyleName ()
  {
    return textFieldStyle.getStyleName ();
  }

  public int getTextFieldHeight ()
  {
    return textFieldStyle.getHeight ();
  }

  public int getTextFieldMaxChars ()
  {
    return textFieldStyle.getMaxChars ();
  }

  public Pattern getTextFieldFilter ()
  {
    return textFieldStyle.getFilter ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ScrollPane Height: [{}] | ScrollPane-TextField Spacing: [{}] | TextField Style: [{}]",
                           super.toString (), scrollPaneHeight, scrollPaneTextFieldSpacing, textFieldStyle);
  }
}
