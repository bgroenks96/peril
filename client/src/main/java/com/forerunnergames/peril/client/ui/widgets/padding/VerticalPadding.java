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
