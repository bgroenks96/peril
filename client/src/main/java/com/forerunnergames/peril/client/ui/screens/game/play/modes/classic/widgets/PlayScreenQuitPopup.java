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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.popup.OkCancelPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;

public final class PlayScreenQuitPopup extends OkCancelPopup
{
  public PlayScreenQuitPopup (final WidgetFactory widgetFactory,
                              final String message,
                              final Stage stage,
                              final PopupListener listener)
  {
    super (widgetFactory,
           PopupStyle.builder ().title ("QUIT?").border (28).buttonSpacing (16).buttonWidth (90).textPadding (16)
                   .textBoxPaddingBottom (20).size (650, 244)
                   .position (587, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 284).message (message).build (),
           stage, listener);

    changeButtonText ("OK", "QUIT");
  }
}
