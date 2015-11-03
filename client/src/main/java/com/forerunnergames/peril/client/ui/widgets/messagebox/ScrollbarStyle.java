package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.forerunnergames.tools.common.Arguments;

public final class ScrollbarStyle
{
  private final Scrollbars scrollbars;
  private final int horizontalHeight;
  private final int verticalWidth;

  public ScrollbarStyle (final Scrollbars scrollbars, final int horizontalHeight, final int verticalWidth)
  {
    Arguments.checkIsNotNull (scrollbars, "scrollbars");
    Arguments.checkIsNotNegative (horizontalHeight, "horizontalHeight");
    Arguments.checkIsNotNegative (verticalWidth, "verticalWidth");

    this.scrollbars = scrollbars;
    this.horizontalHeight = horizontalHeight;
    this.verticalWidth = verticalWidth;
  }

  public enum Scrollbars
  {
    OPTIONAL,
    REQUIRED
  }

  public boolean areScrollbarsRequired ()
  {
    return scrollbars == Scrollbars.REQUIRED;
  }

  public boolean areScrollbarsOptional ()
  {
    return scrollbars == Scrollbars.OPTIONAL;
  }

  public int getHorizontalHeight ()
  {
    return horizontalHeight;
  }

  public int getVerticalWidth ()
  {
    return verticalWidth;
  }
}
