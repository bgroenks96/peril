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
