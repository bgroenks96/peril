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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dice;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;

import com.google.common.collect.ImmutableList;

public interface Dice
{
  boolean DEFAULT_IS_TOUCHABLE = true;

  int getActiveCount ();

  int getWinningCount ();

  int getLosingCount ();

  ImmutableList <DieOutcome> getOutcomes ();

  void roll (final ImmutableList <DieFaceValue> dieFaceValues);

  void setOutcomeAgainst (final ImmutableList <DieFaceValue> competingDieFaceValues);

  // @formatter:off
  /**
   *
   * Clamps the number of dice to within the specified range, according to the following rules:
   *
   * 1) If the number of active dice is already within the specified range, do nothing.
   * 2) If the number of active dice is below the specified range, clamp to the minimum bound.
   * 3) If the number of active dice is above the specified range, clamp to the maximum bound.
   *
   * Clamping is defined as setting limits on how many dice can be added or removed prior to rolling.
   *
   * Clamping affects dice touchability, even if it doesn't change the number of active dice.
   *
   * For example, if there are 2 (out of 3 possible) active dice, with a current range of [2, 2],
   * no dice will be touchable, but clamping to [1, 3] would make active die #2 & inactive die #3 touchable,
   * but would not affect the number of active dice.
   *
   * @param minDieCount The lower inclusive bound on the specified range of allowed active dice.
   * @param maxDieCount The upper inclusive bound on the specified range of allowed active dice.
   *
   */
   // @formatter:on
  void clamp (final int minDieCount, final int maxDieCount);

  void setTouchable (final boolean isTouchable);

  void resetFaceValues ();

  void resetOutcomes ();

  void resetSpinning ();

  void resetAll ();

  void refreshAssets ();

  void update (final float delta);

  Actor asActor ();

  @Override
  String toString ();
}
