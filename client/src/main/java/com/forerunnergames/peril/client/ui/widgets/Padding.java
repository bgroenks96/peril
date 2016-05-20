package com.forerunnergames.peril.client.ui.widgets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class Padding
{
  private int left;
  private int right;
  private int top;
  private int bottom;

  public Padding ()
  {
    left = 0;
    right = 0;
    top = 0;
    bottom = 0;
  }

  public Padding (final int left, final int right, final int top, final int bottom)
  {
    Arguments.checkIsNotNegative (left, "left");
    Arguments.checkIsNotNegative (right, "right");
    Arguments.checkIsNotNegative (top, "top");
    Arguments.checkIsNotNegative (bottom, "bottom");

    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
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

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Left: [{}] | Right: [{}] | Top: [{}] | Bottom: [{}]", getClass ().getSimpleName ());
  }
}
