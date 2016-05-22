package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.forerunnergames.peril.client.ui.widgets.padding.VerticalPadding;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public class MessageBoxStyle
{
  private final String scrollPaneStyle;
  private final ScrollbarStyle scrollbarStyle;
  private final MessageBoxRowStyle rowStyle;
  private final VerticalPadding scrollVPadding = new VerticalPadding ();
  private final VerticalPadding absoluteVPadding = new VerticalPadding ();

  public MessageBoxStyle (final String scrollPaneStyle,
                          final ScrollbarStyle scrollbarStyle,
                          final MessageBoxRowStyle rowStyle,
                          final VerticalPadding scrollVPadding,
                          final VerticalPadding absoluteVPadding)
  {
    Arguments.checkIsNotNull (scrollPaneStyle, "scrollPaneStyle");
    Arguments.checkIsNotNull (scrollbarStyle, "scrollBarStyle");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");
    Arguments.checkIsNotNull (scrollVPadding, "scrollVPadding");
    Arguments.checkIsNotNull (absoluteVPadding, "absoluteVPadding");

    this.scrollPaneStyle = scrollPaneStyle;
    this.scrollbarStyle = scrollbarStyle;
    this.rowStyle = rowStyle;
    this.scrollVPadding.set (scrollVPadding);
    this.absoluteVPadding.set (absoluteVPadding);
  }

  public MessageBoxStyle (final MessageBoxStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    scrollPaneStyle = style.getScrollPaneStyle ();
    scrollbarStyle = style.getScrollbarStyle ();
    rowStyle = style.getRowStyle ();
    scrollVPadding.set (style.getScrollVPadding ());
    absoluteVPadding.set (style.getAbsoluteVPadding ());
  }

  public String getScrollPaneStyle ()
  {
    return scrollPaneStyle;
  }

  public ScrollbarStyle getScrollbarStyle ()
  {
    return scrollbarStyle;
  }

  public boolean areScrollbarsRequired ()
  {
    return scrollbarStyle.areScrollbarsRequired ();
  }

  public MessageBoxRowStyle getRowStyle ()
  {
    return rowStyle;
  }

  public int getRowHeight ()
  {
    return rowStyle.getHeight ();
  }

  public int getRowPaddingLeft ()
  {
    return rowStyle.getPaddingLeft ();
  }

  public int getRowPaddingRight ()
  {
    return rowStyle.getPaddingRight ();
  }

  public VerticalPadding getScrollVPadding ()
  {
    return scrollVPadding;
  }

  public int getScrollPaddingTop ()
  {
    return scrollVPadding.getTop ();
  }

  public int getScrollPaddingBottom ()
  {
    return scrollVPadding.getBottom ();
  }

  public VerticalPadding getAbsoluteVPadding ()
  {
    return absoluteVPadding;
  }

  public int getAbsolutePaddingTop ()
  {
    return absoluteVPadding.getTop ();
  }

  public int getAbsolutePaddingBottom ()
  {
    return absoluteVPadding.getBottom ();
  }

  @Override
  public String toString ()
  {
    return Strings
            .format ("{}: ScrollPane Style: [{}] | Scrollbar Style: [{}] | Row Style: [{}] | Scroll V-Padding: [{}]"
                             + " | Absolute V-Padding: [{}]", getClass ().getSimpleName (), scrollPaneStyle,
                     scrollbarStyle,
                     rowStyle, scrollVPadding, absoluteVPadding);
  }
}
