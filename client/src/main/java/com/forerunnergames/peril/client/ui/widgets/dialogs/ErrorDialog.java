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

package com.forerunnergames.peril.client.ui.widgets.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public final class ErrorDialog extends OkDialog
{
  public ErrorDialog (final WidgetFactory widgetFactory, final Stage stage, final DialogListener listener)
  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
                    .size (650, 388)
                    .title ("ERROR")
                    .titleHeight (51)
                    .border (28)
                    .buttonSpacing (16)
                    .buttonWidth (90)
                    .textBoxPaddingHorizontal (2)
                    .textBoxPaddingBottom (21)
                    .textPaddingHorizontal (4)
                    .textPaddingBottom (4)
                    .build (),
            stage, listener);
    // @formatter:on
  }

  public ErrorDialog (final WidgetFactory widgetFactory,
                      final Stage stage,
                      final String submitButtonText,
                      final DialogListener listener)
  {
    this (widgetFactory, stage, listener);

    changeButtonText ("OK", submitButtonText);
  }
}
