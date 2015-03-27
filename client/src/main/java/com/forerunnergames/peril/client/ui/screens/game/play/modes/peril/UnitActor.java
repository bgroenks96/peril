package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import javax.annotation.Nullable;

public interface UnitActor
{
  enum MovementDirection
  {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE
  }

  enum FacingDirection
  {
    UP(0),
    DOWN(180),
    LEFT(270),
    RIGHT(90);

    public int degrees ()
    {
      return degrees;
    }

    FacingDirection (final int degrees)
    {
      this.degrees = degrees;
    }

    private final int degrees;
  }

  void moveRight ();

  void moveLeft ();

  void moveUp ();

  void moveDown ();

  void setMoving (final MovementDirection movementDirection);

  void setFacing (final FacingDirection facingDirection);

  boolean isMoving ();

  Vector2 getPreviousPosition ();

  Vector2 getCurrentPosition ();

  Actor asActor ();

  boolean is (@Nullable final Actor actor);

  boolean isNot (@Nullable final Actor actor);
}
