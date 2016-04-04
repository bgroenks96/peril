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

import com.forerunnergames.tools.common.Arguments;

public final class SideBarButton
{
  private final ImageButton button;
  private final ButtonType type;

  public SideBarButton (final ImageButton button, final ButtonType type)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (type, "type");

    this.button = button;
    this.type = type;
  }

  public enum ButtonType
  {
    TRADE_IN ("trade-in"),
    REINFORCE ("reinforce"),
    END_TURN ("end-turn"),
    MY_SETTINGS ("my-settings");

    private final String styleName;

    ButtonType (final String styleName)
    {
      this.styleName = styleName;
    }

    public String getStyleName ()
    {
      return styleName;
    }
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
