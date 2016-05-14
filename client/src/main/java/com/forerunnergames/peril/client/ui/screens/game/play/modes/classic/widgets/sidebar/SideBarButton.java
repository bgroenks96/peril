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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.sidebar;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.tools.common.Arguments;

public final class SideBarButton
{
  private final ImageButton button;
  private final ButtonType type;

  public enum ButtonType
  {
    TRADE_IN (StyleSettings.PLAY_SCREEN_SIDE_BAR_TRADE_IN_IMAGE_BUTTON_STYLE),
    REINFORCE (StyleSettings.PLAY_SCREEN_SIDE_BAR_REINFORCE_IMAGE_BUTTON_STYLE),
    END_TURN (StyleSettings.PLAY_SCREEN_SIDE_BAR_END_TURN_IMAGE_BUTTON_STYLE),
    MY_SETTINGS (StyleSettings.PLAY_SCREEN_SIDE_BAR_MY_SETTINGS_IMAGE_BUTTON_STYLE);

    private final String styleName;

    public String getImageButtonStyleName ()
    {
      return styleName;
    }

    ButtonType (final String styleName)
    {
      this.styleName = styleName;
    }
  }

  public SideBarButton (final ImageButton button, final ButtonType type)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (type, "type");

    this.button = button;
    this.type = type;
  }

  public Actor asActor ()
  {
    return button;
  }

  public ButtonType getType ()
  {
    return type;
  }

  public void setStyle (final ImageButton.ImageButtonStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    button.setStyle (style);
  }
}
