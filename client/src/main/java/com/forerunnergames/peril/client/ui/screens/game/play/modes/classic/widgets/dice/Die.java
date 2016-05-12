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
import com.badlogic.gdx.scenes.scene2d.Touchable;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;

public interface Die extends Comparable <Die>
{
  DieState DEFAULT_STATE = DieState.ENABLED;
  DieOutcome DEFAULT_OUTCOME = DieOutcome.NONE;
  Touchable DEFAULT_TOUCHABLE = Touchable.disabled;
  Die NULL_DIE = new NullDie ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();

  int getIndex ();

  void roll (final DieFaceValue faceValue);

  void setOutcome (final DieOutcome outcome);

  void enable ();

  void disable ();

  void setTouchable (final boolean isTouchable);

  void resetSpinning ();

  void addListener (final DieListener listener);

  void resetState ();

  void resetFaceValue ();

  void resetOutcome ();

  void resetAll ();

  void refreshAssets ();

  void update (final float delta);

  float getWidth ();

  float getHeight ();

  Actor asActor ();
}
