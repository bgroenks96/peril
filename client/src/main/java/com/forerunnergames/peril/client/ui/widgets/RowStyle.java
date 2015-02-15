package com.forerunnergames.peril.client.ui.widgets;

import com.forerunnergames.tools.common.Arguments;

public final class RowStyle
{
  private final float height;
  private final float paddingLeft;
  private final float paddingRight;

  public RowStyle (final float height, final float paddingLeft, final float paddingRight)
  {
    Arguments.checkIsNotNegative (height, "height");
    Arguments.checkIsNotNegative (paddingLeft, "paddingLeft");
    Arguments.checkIsNotNegative (paddingRight, "paddingRight");

    this.height = height;
    this.paddingLeft = paddingLeft;
    this.paddingRight = paddingRight;
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
