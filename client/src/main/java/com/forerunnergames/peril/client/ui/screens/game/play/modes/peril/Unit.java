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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import javax.annotation.Nullable;

public interface Unit extends InputProcessor
{
  enum MovementDirection
  {
    FORWARD,
    REVERSE,
    NONE
  }

  enum TurnDirection
  {
    RIGHT,
    LEFT,
    U_TURN,
    NONE
  }

  void turnRight ();

  void turnLeft ();

  void turnAround ();

  void moveForward ();

  void moveReverse ();

  void setMoving (final MovementDirection movementDirection);

  void setTurning (final TurnDirection turnDirection);

  Vector2 getPreviousPosition ();

  Vector2 getCurrentPosition ();

  Actor asActor ();

  boolean is (@Nullable final Actor actor);
}
