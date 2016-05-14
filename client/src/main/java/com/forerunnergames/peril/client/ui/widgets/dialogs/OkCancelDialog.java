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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;

public class OkCancelDialog extends OkDialog
{
  public OkCancelDialog (final WidgetFactory widgetFactory,
                         final DialogStyle dialogStyle,
                         final Stage stage,
                         final DialogListener listener)
  {
    super (widgetFactory, dialogStyle, stage, listener);
  }

  @Override
  protected void addButtons ()
  {
    addTextButton ("CANCEL", DialogAction.HIDE);

    super.addButtons ();
  }

  @Override
  protected void addKeys ()
  {
    super.addKeys ();

    addKey (Input.Keys.ESCAPE, DialogAction.HIDE);
  }
}
