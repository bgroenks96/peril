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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.error;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.PlayMapBlockingDialog;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.ErrorDialog;

public final class PlayScreenErrorDialog extends ErrorDialog implements PlayMapBlockingDialog
{
  public PlayScreenErrorDialog (final WidgetFactory widgetFactory, final Stage stage, final DialogListener listener)
  {
    this (widgetFactory, DialogStyle.AUTO_H_CENTER, DialogStyle.AUTO_V_CENTER, stage, listener);
  }

  public PlayScreenErrorDialog (final WidgetFactory widgetFactory,
                                final int positionUpperLeftReferenceScreenSpaceX,
                                final int positionUpperLeftReferenceScreenSpacey,
                                final Stage stage,
                                final DialogListener listener)
  {
    super (widgetFactory, positionUpperLeftReferenceScreenSpaceX, positionUpperLeftReferenceScreenSpacey, stage,
           listener);
  }
}
