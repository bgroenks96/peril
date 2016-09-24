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

package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.tools.common.Arguments;

abstract class HumanPlayerColorIconWidgetFactory extends AbstractPlayerColorIconWidgetFactory
{
  HumanPlayerColorIconWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  public Button.ButtonStyle createPlayerColorIconStyle (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    return createButtonStyle (StyleSettings.HUMAN_PLAYER_COLOR_ICON_STYLE_PREFIX + color.toLowerCase ());
  }
}
