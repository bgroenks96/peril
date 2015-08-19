package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.tools.common.Arguments;

public final class NullDrawable implements Drawable
{
  @Override
  public void draw (final Batch batch, final float x, final float y, final float width, final float height)
  {
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNegative (width, "width");
    Arguments.checkIsNotNegative (height, "height");
  }

  @Override
  public float getLeftWidth ()
  {
    return 1.0f;
  }

  @Override
  public void setLeftWidth (final float leftWidth)
  {
    Arguments.checkIsNotNegative (leftWidth, "leftWidth");
  }

  @Override
  public float getRightWidth ()
  {
    return 1.0f;
  }

  @Override
  public void setRightWidth (final float rightWidth)
  {
    Arguments.checkIsNotNegative (rightWidth, "rightWidth");
  }

  @Override
  public float getTopHeight ()
  {
    return 1.0f;
  }

  @Override
  public void setTopHeight (final float topHeight)
  {
    Arguments.checkIsNotNegative (topHeight, "topHeight");
  }

  @Override
  public float getBottomHeight ()
  {
    return 1.0f;
  }

  @Override
  public void setBottomHeight (final float bottomHeight)
  {
    Arguments.checkIsNotNegative (bottomHeight, "bottomHeight");
  }

  @Override
  public float getMinWidth ()
  {
    return 1.0f;
  }

  @Override
  public void setMinWidth (final float minWidth)
  {
    Arguments.checkIsNotNegative (minWidth, "minWidth");
  }

  @Override
  public float getMinHeight ()
  {
    return 1.0f;
  }

  @Override
  public void setMinHeight (final float minHeight)
  {
    Arguments.checkIsNotNegative (minHeight, "minHeight");
  }
}
