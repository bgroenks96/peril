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
