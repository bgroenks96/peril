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

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;

abstract class DefenderDie extends AbstractDie
{
  private final GameRules rules;

  protected DefenderDie (final int index, final ImageButton button, final GameRules rules)
  {
    super (index, GameSettings.DEFAULT_DIE_FACE_VALUE, button);

    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  protected DieOutcome determineOutcome (final DieFaceValue thisFaceValue, final DieFaceValue thatFaceValue)
  {
    Arguments.checkIsNotNull (thisFaceValue, "thisFaceValue");
    Arguments.checkIsNotNull (thatFaceValue, "thatFaceValue");

    return rules.determineDefenderOutcome (thisFaceValue, thatFaceValue);
  }
}
