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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyText;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.math.IntMath;

public final class CountryArmyTextEffects extends DefaultCountryArmyText
{
  private static final float MOVE_BY_X = -80.0f;
  private static final float MOVE_BY_Y = 80.0f;
  private static final float MOVE_TIME_SECONDS = 1.5f;
  private final HorizontalMoveDirection direction;

  public CountryArmyTextEffects (final BitmapFont font, final HorizontalMoveDirection direction)
  {
    super (font);

    Arguments.checkIsNotNull (direction, "direction");

    this.direction = direction;
  }

  enum HorizontalMoveDirection
  {
    LEFT (1.0f),
    RIGHT (-1.0f);

    private final float sign;

    public float getSign ()
    {
      return sign;
    }

    HorizontalMoveDirection (final float sign)
    {
      this.sign = sign;
    }
  }

  @Override
  public void changeArmiesBy (final int deltaArmies)
  {
    final int oldArmies = getArmies ();
    final int newArmies = IntMath.checkedAdd (oldArmies, deltaArmies);

    setArmies (newArmies);

    if (deltaArmies == 0) return;

    setText ((deltaArmies > 0 ? "+" : "") + deltaArmies);

    // @formatter:off
    addAction (
            Actions.sequence (
                    Actions.show (),
                    Actions.parallel (
                            Actions.moveBy (direction.getSign () * MOVE_BY_X, MOVE_BY_Y, MOVE_TIME_SECONDS),
                            Actions.fadeOut (MOVE_TIME_SECONDS, Interpolation.fade)),
                    Actions.hide (),
                    Actions.parallel (
                            Actions.alpha (1.0f),
                            Actions.moveBy (-direction.getSign () * MOVE_BY_X, -MOVE_BY_Y))));
    // @formatter:on
  }
}
