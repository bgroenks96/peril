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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import com.forerunnergames.tools.common.Arguments;

public final class Tank2 extends Actor implements Unit
{
  private final Actor body;
  private final Actor turret;
  private final Vector2 bodyForwardVector = new Vector2 (0, 1);
  private final Vector2 turretForwardVector = new Vector2 (0, 1);
  private final Vector2 previousPosition = new Vector2 (0, 0);
  private final Vector2 currentPosition = new Vector2 (0, 0);
  private MovementDirection movementDirection = MovementDirection.NONE;
  private TurnDirection turnDirection = TurnDirection.NONE;
  private TurnDirection turretTurnDirection = TurnDirection.NONE;
  private boolean turretActive = false;
  private Action forwardBodyAction;
  private Action reverseBodyAction;
  private Action forwardTurretAction;
  private Action reverseTurretAction;

  public Tank2 (final TankBody body, final TankTurret turret)
  {
    Arguments.checkIsNotNull (body, "body");
    Arguments.checkIsNotNull (turret, "turret");

    this.body = body;
    this.turret = turret;
  }

  @Override
  public void turnRight ()
  {
  }

  @Override
  public void turnLeft ()
  {
  }

  @Override
  public void turnAround ()
  {
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
  public void setMoving (final MovementDirection movementDirection)
  {
    Arguments.checkIsNotNull (movementDirection, "movementDirection");

    if (turnDirection != TurnDirection.NONE || this.movementDirection != MovementDirection.NONE) return;

    switch (movementDirection)
    {
      case FORWARD:
      {
        body.addAction (Actions
                .sequence (Actions.moveBy (bodyForwardVector.x * BattleGridSettings.BATTLE_GRID_CELL_WIDTH,
                                           -bodyForwardVector.y * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1),
                           Actions.run (new Runnable ()
                           {
                             @Override
                             public void run ()
                             {
                               Tank2.this.movementDirection = MovementDirection.NONE;
                             }
                           })));

        turret.addAction (Actions.moveBy (bodyForwardVector.x * BattleGridSettings.BATTLE_GRID_CELL_WIDTH,
                                          -bodyForwardVector.y * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        break;
      }
      case REVERSE:
      {
        body.addAction (Actions
                .sequence (Actions.moveBy (-bodyForwardVector.x * BattleGridSettings.BATTLE_GRID_CELL_WIDTH,
                                           bodyForwardVector.y * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1),
                           Actions.run (new Runnable ()
                           {
                             @Override
                             public void run ()
                             {
                               Tank2.this.movementDirection = MovementDirection.NONE;
                             }
                           })));

        turret.addAction (Actions.moveBy (-bodyForwardVector.x * BattleGridSettings.BATTLE_GRID_CELL_WIDTH,
                                          bodyForwardVector.y * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        break;
      }
    }

    this.movementDirection = movementDirection;
  }

  @Override
  public void setTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    if (movementDirection != MovementDirection.NONE || this.turnDirection != TurnDirection.NONE) return;

    switch (turnDirection)
    {
      case RIGHT:
      {
        body.addAction (Actions.sequence (Actions.rotateBy (-90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            Tank2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (-90);
          }
        })));

        break;
      }
      case LEFT:
      {
        body.addAction (Actions.sequence (Actions.rotateBy (90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            Tank2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (90);
          }
        })));

        break;
      }
      case U_TURN:
      {
        body.addAction (Actions.sequence (Actions.rotateBy (-180, 2), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            Tank2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (-180);
          }
        })));

        break;
      }
    }

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

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    body.draw (batch, parentAlpha);
    turret.draw (batch, parentAlpha);
  }

  @Override
  public void act (final float delta)
  {
    super.act (delta);

    body.act (delta);
    turret.act (delta);
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        setMoving (MovementDirection.NONE);
        setTurning (TurnDirection.LEFT);

        if (!turretActive) setTurretTurning (TurnDirection.LEFT);

        return true;
      }
      case Input.Keys.RIGHT:
      {
        setMoving (MovementDirection.NONE);
        setTurning (TurnDirection.RIGHT);

        if (!turretActive) setTurretTurning (TurnDirection.RIGHT);

        return true;
      }
      case Input.Keys.U:
      {
        setMoving (MovementDirection.NONE);
        setTurning (TurnDirection.U_TURN);

        if (!turretActive) setTurretTurning (TurnDirection.U_TURN);

        return true;
      }
      case Input.Keys.UP:
      {
        switch (movementDirection)
        {
          case NONE:
          {
            setMoving (MovementDirection.FORWARD);
            break;
          }
          case REVERSE:
          {
            setMoving (MovementDirection.NONE);
            break;
          }
        }

        return true;
      }
      case Input.Keys.DOWN:
      {
        switch (movementDirection)
        {
          case NONE:
          {
            setMoving (MovementDirection.REVERSE);
            break;
          }
          case FORWARD:
          {
            setMoving (MovementDirection.NONE);
            break;
          }
        }

        return true;
      }
      case Input.Keys.A:
      {
        switch (turretTurnDirection)
        {
          case NONE:
          {
            setTurretTurning (TurnDirection.LEFT);
            break;
          }
          case RIGHT:
          {
            setTurretTurning (TurnDirection.NONE);
            break;
          }
        }

        turretActive = true;

        return true;
      }
      case Input.Keys.S:
      {
        switch (turretTurnDirection)
        {
          case NONE:
          {
            setTurretTurning (TurnDirection.RIGHT);
            break;
          }
          case LEFT:
          {
            setTurretTurning (TurnDirection.NONE);
            break;
          }
        }

        turretActive = true;

        return true;
      }
      case Input.Keys.W:
      {
        switch (turretTurnDirection)
        {
          case NONE:
          {
            setTurretTurning (TurnDirection.U_TURN);
            break;
          }
          case LEFT:
          {
            setTurretTurning (TurnDirection.NONE);
            break;
          }
          case RIGHT:
          {
            setTurretTurning (TurnDirection.NONE);
            break;
          }
        }

        turretActive = true;

        return true;
      }
      case Input.Keys.X:
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

  public void setTurretTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    if (turretTurnDirection != TurnDirection.NONE) return;

    switch (turnDirection)
    {
      case RIGHT:
      {
        turret.addAction (Actions.sequence (Actions.rotateBy (-90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (-90);
          }
        })));

        break;
      }
      case LEFT:
      {
        turret.addAction (Actions.sequence (Actions.rotateBy (90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (90);
          }
        })));

        break;
      }
      case U_TURN:
      {
        turret.addAction (Actions.sequence (Actions.rotateBy (-180, 2), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (-180);
          }
        })));

        break;
      }
    }

    turretTurnDirection = turnDirection;
  }

  public void deactivateTurret ()
  {
    if (!turretActive) return;

    final int degrees = Math.round (turretForwardVector.angle (bodyForwardVector));

    if (degrees == -90)
    {
      setTurretTurning (TurnDirection.RIGHT);
    }
    else if (degrees == 90)
    {
      setTurretTurning (TurnDirection.LEFT);
    }
    else if (Math.abs (degrees) == 180)
    {
      setTurretTurning (TurnDirection.U_TURN);
    }

    turretActive = false;
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
