/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.widgets.messagebox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class MessageBoxRowHighlighting
{
  private final WidgetFactory widgetFactory;
  private final Image image;

  public MessageBoxRowHighlighting (final Image image, final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
    this.image = image;
  }

  public Actor asActor ()
  {
    return image;
  }

  public void refreshAssets ()
  {
    image.setDrawable (widgetFactory.createMessageBoxRowHighlightingDrawable ());
  }

  public boolean isVisible ()
  {
    return image.isVisible ();
  }

  public void setVisible (final boolean isVisible)
  {
    image.setVisible (isVisible);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Image: {} | Visible: {}", getClass ().getSimpleName (), image, image.isVisible ());
  }
}
