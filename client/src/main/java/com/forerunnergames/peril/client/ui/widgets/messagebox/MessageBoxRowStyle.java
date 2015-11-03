package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.forerunnergames.tools.common.Arguments;

public final class MessageBoxRowStyle
{
  private final String labelStyleName;
  private final int labelAlignment;
  private final float height;
  private final float paddingLeft;
  private final float paddingRight;

  public MessageBoxRowStyle (final String labelStyleName,
                             final int labelAlignment,
                             final float height,
                             final float paddingLeft,
                             final float paddingRight)
  {
    Arguments.checkIsNotNull (labelStyleName, "labelStyleName");
    Arguments.checkIsNotNegative (height, "height");
    Arguments.checkIsNotNegative (paddingLeft, "paddingLeft");
    Arguments.checkIsNotNegative (paddingRight, "paddingRight");

    this.labelStyleName = labelStyleName;
    this.labelAlignment = labelAlignment;
    this.height = height;
    this.paddingLeft = paddingLeft;
    this.paddingRight = paddingRight;
  }

  public String getLabelStyleName ()
  {
    return labelStyleName;
  }

  public int getLabelAlignment ()
  {
    return labelAlignment;
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
