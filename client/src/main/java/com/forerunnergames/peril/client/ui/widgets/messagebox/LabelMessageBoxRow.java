/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public class LabelMessageBoxRow <T extends Message> implements MessageBoxRow <T>
{
  private final MessageBoxRowStyle rowStyle;
  private final WidgetFactory widgetFactory;
  private final Label label;
  private T message;

  public LabelMessageBoxRow (final T message, final MessageBoxRowStyle rowStyle, final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.message = message;
    this.rowStyle = rowStyle;
    this.widgetFactory = widgetFactory;

    label = widgetFactory.createWrappingLabel (message.getText (), rowStyle.getLabelAlignment (),
                                               rowStyle.getLabelStyleName ());
  }

  public LabelMessageBoxRow (final T message,
                             final Label label,
                             final MessageBoxRowStyle rowStyle,
                             final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (label, "label");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.message = message;
    this.label = label;
    this.rowStyle = rowStyle;
    this.widgetFactory = widgetFactory;
  }

  @Override
  public T getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    label.setStyle (widgetFactory.createLabelStyle (rowStyle.getLabelStyleName ()));
  }

  @Override
  public Actor asActor ()
  {
    return label;
  }

  protected String parse (final T message)
  {
    Arguments.checkIsNotNull (message, "message");

    return message.getText ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Message: {} | Label: {} | Row Style: {}", getClass ().getSimpleName (), message, label,
                           rowStyle);
  }
}
