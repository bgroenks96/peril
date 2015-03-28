package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import javax.annotation.Nullable;

public interface UnitActor extends InputProcessor
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
