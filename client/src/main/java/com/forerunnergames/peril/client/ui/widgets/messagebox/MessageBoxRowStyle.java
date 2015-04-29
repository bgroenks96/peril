package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.forerunnergames.tools.common.Arguments;

public final class MessageBoxRowStyle
{
  private final float height;
  private final float paddingLeft;
  private final float paddingRight;

  public MessageBoxRowStyle (final float height, final float paddingLeft, final float paddingRight)
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
