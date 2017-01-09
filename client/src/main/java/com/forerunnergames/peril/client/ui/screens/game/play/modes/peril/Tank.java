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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.tools.common.Arguments;

public final class Tank extends Actor implements Unit
{
  private static final int SPEED_IN_GRID_SQUARES_PER_SECOND = 2;
  private static final float ACTION_THRESHOLD_SECONDS = 1.0f / SPEED_IN_GRID_SQUARES_PER_SECOND;
  private final Sprite bodySprite;
  private final Sprite turretSprite;
  private final Vector2 spritePosition = new Vector2 (0, 0);
  private final Vector2 bodyForwardVector = new Vector2 (0, 1);
  private final Vector2 turretForwardVector = new Vector2 (0, 1);
  private final Vector2 previousPosition = new Vector2 (0, 0);
  private final Vector2 currentPosition = new Vector2 (0, 0);
  private MovementDirection movementDirection = MovementDirection.NONE;
  private TurnDirection turnDirection = TurnDirection.NONE;
  private TurnDirection turretTurnDirection = TurnDirection.NONE;
  private float timeSinceLastActionSeconds = 0.0f;
  private boolean completedTurningAround = false;
  private boolean completedTurningTurretAround = false;
  private boolean turretActive = false;

  public Tank (final Sprite bodySprite, final Sprite turretSprite)
  {
    Arguments.checkIsNotNull (bodySprite, "bodySprite");
    Arguments.checkIsNotNull (turretSprite, "turretSprite");

    this.bodySprite = bodySprite;
    this.turretSprite = turretSprite;

    bodySprite.setOrigin (bodySprite.getWidth () / 2.0f, 22);
    turretSprite.setOrigin (turretSprite.getWidth () / 2.0f, 22);
  }

  @Override
  public void turnRight ()
  {
    bodySprite.rotate (-90);

    if (!turretActive)
    {
      turretSprite.rotate (-90);
      turretForwardVector.rotate90 (1);
    }

    bodyForwardVector.rotate90 (1);

    turnDirection = TurnDirection.NONE;
  }

  @Override
  public void turnLeft ()
  {
    bodySprite.rotate (90);
    bodyForwardVector.rotate90 (-1);

    if (!turretActive)
    {
      turretSprite.rotate (90);
      turretForwardVector.rotate90 (-1);
    }

    turnDirection = TurnDirection.NONE;
  }

  @Override
  public void turnAround ()
  {
    bodySprite.rotate (-90);
    bodyForwardVector.rotate90 (1);

    if (!turretActive)
    {
      turretSprite.rotate (-90);
      turretForwardVector.rotate90 (1);
    }

    if (completedTurningAround) turnDirection = TurnDirection.NONE;

    completedTurningAround = !completedTurningAround;
  }

  @Override
  public void moveForward ()
  {
    previousPosition.set (currentPosition);
    currentPosition.sub (bodyForwardVector);

    clampPosition ();
  }

  @Override
  public void moveReverse ()
  {
    previousPosition.set (currentPosition);
    currentPosition.add (bodyForwardVector);

    clampPosition ();
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    spritePosition.set ((BattleGridSettings.BATTLE_GRID_CELL_WIDTH - bodySprite.getWidth ()) / 2.0f,
                        (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT - bodySprite.getHeight ()) / 2.0f);
    localToParentCoordinates (spritePosition);
    bodySprite.setPosition (spritePosition.x, spritePosition.y);
    turretSprite.setPosition (spritePosition.x, spritePosition.y + 3);
    bodySprite.draw (batch);
    turretSprite.draw (batch);
  }

  @Override
  public void act (final float delta)
  {
    super.act (delta);

    timeSinceLastActionSeconds += delta;

    if (timeSinceLastActionSeconds < ACTION_THRESHOLD_SECONDS) return;

    timeSinceLastActionSeconds = 0.0f;

    if (isTurretTurning ()) executeTurretTurn ();

    if (isTurning ())
    {
      executeTurn ();
    }
    else if (isMoving ())
    {
      executeMovement ();
    }
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
        {
          switch (turnDirection)
          {
            case RIGHT:
            {
              setTurning (Unit.TurnDirection.NONE);
              break;
            }
            case NONE:
            {
              setTurning (Unit.TurnDirection.LEFT);
              break;
            }
          }
        }
        else
        {
          turnLeft ();
        }

        return true;
      }
      case Input.Keys.RIGHT:
      {
        if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
        {
          switch (turnDirection)
          {
            case LEFT:
            {
              setTurning (Unit.TurnDirection.NONE);
              break;
            }
            case NONE:
            {
              setTurning (Unit.TurnDirection.RIGHT);
              break;
            }
          }
        }
        else
        {
          turnRight ();
        }

        return true;
      }
      case Input.Keys.U:
      {
        if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
        {
          if (!isTurning ()) setTurning (Unit.TurnDirection.U_TURN);
        }
        else
        {
          turnAround ();
        }

        return true;
      }
      case Input.Keys.UP:
      {
        if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
        {
          switch (movementDirection)
          {
            case REVERSE:
            {
              setMoving (Unit.MovementDirection.NONE);
              break;
            }
            case NONE:
            {
              setMoving (Unit.MovementDirection.FORWARD);
              break;
            }
          }
        }
        else
        {
          moveForward ();
        }

        return true;
      }
      case Input.Keys.DOWN:
      {
        if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
        {
          switch (movementDirection)
          {
            case FORWARD:
            {
              setMoving (Unit.MovementDirection.NONE);
              break;
            }
            case NONE:
            {
              setMoving (Unit.MovementDirection.REVERSE);
              break;
            }
          }
        }
        else
        {
          moveReverse ();
        }

        return true;
      }
      case Input.Keys.A:
      {
        setTurretTurning (TurnDirection.LEFT);
        turretActive = true;

        return true;
      }
      case Input.Keys.S:
      {
        setTurretTurning (TurnDirection.RIGHT);
        turretActive = true;

        return true;
      }
      case Input.Keys.W:
      {
        deactivateTurret ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean keyUp (final int keycode)
  {
    return false;
  }

  @Override
  public boolean keyTyped (final char character)
  {
    return false;
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    return false;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return false;
  }

  @Override
  public boolean touchDragged (final int screenX, final int screenY, final int pointer)
  {
    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    return false;
  }

  @Override
  public boolean scrolled (final int amount)
  {
    return false;
  }

  public void turnTurretRight ()
  {
    turretSprite.rotate (-90);
    turretForwardVector.rotate90 (1);

    turretTurnDirection = TurnDirection.NONE;
    turretActive = true;
  }

  public void turnTurretLeft ()
  {
    turretSprite.rotate (90);
    turretForwardVector.rotate90 (-1);

    turretTurnDirection = TurnDirection.NONE;
    turretActive = true;
  }

  public void turnTurretAround ()
  {
    turretSprite.rotate (-90);
    turretForwardVector.rotate90 (1);

    if (completedTurningTurretAround) turretTurnDirection = TurnDirection.NONE;

    completedTurningTurretAround = !completedTurningTurretAround;
    turretActive = true;
  }

  public void deactivateTurret ()
  {
    if (!turretActive) return;

    final int degrees = Math.round (turretForwardVector.angle (bodyForwardVector));

    if (degrees == 0) return;

    if (degrees == 90)
    {
      setTurretTurning (TurnDirection.RIGHT);
    }
    else if (degrees == -90)
    {
      setTurretTurning (TurnDirection.LEFT);
    }
    else if (Math.abs (degrees) == 180)
    {
      setTurretTurning (TurnDirection.U_TURN);
    }

    turretActive = false;
  }

  private boolean isMoving ()
  {
    return movementDirection != MovementDirection.NONE;
  }

  @Override
  public void setMoving (final MovementDirection movementDirection)
  {
    Arguments.checkIsNotNull (movementDirection, "movementDirection");

    this.movementDirection = movementDirection;
  }

  private boolean isTurning ()
  {
    return turnDirection != TurnDirection.NONE;
  }

  @Override
  public void setTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    this.turnDirection = turnDirection;
  }

  @Override
  public Vector2 getPreviousPosition ()
  {
    return previousPosition;
  }

  @Override
  public Vector2 getCurrentPosition ()
  {
    return currentPosition;
  }

  @Override
  public Actor asActor ()
  {
    return this;
  }

  @Override
  public boolean is (final Actor actor)
  {
    return equals (actor);
  }

  private boolean isTurretTurning ()
  {
    return turretTurnDirection != TurnDirection.NONE;
  }

  public void setTurretTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    turretTurnDirection = turnDirection;
  }

  private void executeTurn ()
  {
    switch (turnDirection)
    {
      case RIGHT:
      {
        turnRight ();
        break;
      }
      case LEFT:
      {
        turnLeft ();
        break;
      }
      case U_TURN:
      {
        turnAround ();
        break;
      }
    }
  }

  private void executeTurretTurn ()
  {
    switch (turretTurnDirection)
    {
      case RIGHT:
      {
        turnTurretRight ();
        break;
      }
      case LEFT:
      {
        turnTurretLeft ();
        break;
      }
      case U_TURN:
      {
        turnTurretAround ();
        break;
      }
    }
  }

  private void executeMovement ()
  {
    switch (movementDirection)
    {
      case FORWARD:
      {
        moveForward ();
        return;
      }
      case REVERSE:
      {
        moveReverse ();
        return;
      }
      default:
      {
        break;
      }
    }
  }

  private void clampPosition ()
  {
    if (currentPosition.x < BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX)
      currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX;
    if (currentPosition.x > BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX)
      currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX;
    if (currentPosition.y < BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX)
      currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX;
    if (currentPosition.y > BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX)
      currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX;
  }
}
