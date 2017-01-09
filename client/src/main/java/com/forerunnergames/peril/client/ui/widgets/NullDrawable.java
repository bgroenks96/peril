/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
