package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;

import com.forerunnergames.tools.common.Arguments;

public final class TankActor2 extends Actor implements UnitActor
{
  private final Actor bodyActor;
  private final Actor turretActor;
  private Vector2 bodyForwardVector = new Vector2 (0, 1);
  private Vector2 turretForwardVector = new Vector2 (0, 1);
  private Vector2 previousPosition = new Vector2 (0, 0);
  private Vector2 currentPosition = new Vector2 (0, 0);
  private MovementDirection movementDirection = MovementDirection.NONE;
  private TurnDirection turnDirection = TurnDirection.NONE;
  private TurnDirection turretTurnDirection = TurnDirection.NONE;
  private boolean turretActive = false;
  private RepeatAction forwardBodyAction = new RepeatAction ();
  private RepeatAction reverseBodyAction = new RepeatAction ();
  private RepeatAction forwardTurretAction = new RepeatAction ();
  private RepeatAction reverseTurretAction = new RepeatAction ();

  public TankActor2 ()
  {
    bodyActor = new TankBodyActor ();
    turretActor = new TankTurretActor ();
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    bodyActor.draw (batch, parentAlpha);
    turretActor.draw (batch, parentAlpha);
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    bodyActor.act (delta);
    turretActor.act (delta);
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

        if (! turretActive) setTurretTurning (TurnDirection.LEFT);

        return true;
      }
      case Input.Keys.RIGHT:
      {
        setMoving (MovementDirection.NONE);
        setTurning (TurnDirection.RIGHT);

        if (! turretActive) setTurretTurning (TurnDirection.RIGHT);

        return true;
      }
      case Input.Keys.U:
      {
        setMoving (MovementDirection.NONE);
        setTurning (TurnDirection.U_TURN);

        if (! turretActive) setTurretTurning (TurnDirection.U_TURN);

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
            setTurretTurning (TurnDirection.RIGHT);
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

    if (this.turnDirection != TurnDirection.NONE) return;

    switch (movementDirection)
    {
      case NONE:
      {
        forwardBodyAction.finish ();
        reverseBodyAction.finish ();
        forwardTurretAction.finish ();
        reverseTurretAction.finish ();

        break;
      }
      case FORWARD:
      {
        forwardBodyAction = Actions.forever (Actions.moveBy (bodyForwardVector.x
            * BattleGridSettings.BATTLE_GRID_CELL_WIDTH, - bodyForwardVector.y
            * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        forwardTurretAction = Actions.forever (Actions.moveBy (bodyForwardVector.x
            * BattleGridSettings.BATTLE_GRID_CELL_WIDTH, - bodyForwardVector.y
            * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        bodyActor.removeAction (reverseBodyAction);
        turretActor.removeAction (reverseTurretAction);

        bodyActor.addAction (Actions.sequence (forwardBodyAction, Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.movementDirection = MovementDirection.NONE;
          }
        })));

        turretActor.addAction (forwardTurretAction);

        break;
      }
      case REVERSE:
      {
        reverseBodyAction = Actions.forever (Actions.moveBy (- bodyForwardVector.x
            * BattleGridSettings.BATTLE_GRID_CELL_WIDTH, bodyForwardVector.y
            * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        reverseTurretAction = Actions.forever (Actions.moveBy (- bodyForwardVector.x
            * BattleGridSettings.BATTLE_GRID_CELL_WIDTH, bodyForwardVector.y
            * BattleGridSettings.BATTLE_GRID_CELL_HEIGHT, 1));

        bodyActor.removeAction (forwardBodyAction);
        turretActor.removeAction (forwardTurretAction);

        bodyActor.addAction (Actions.sequence (reverseBodyAction, Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.movementDirection = MovementDirection.NONE;
          }
        })));

        turretActor.addAction (reverseTurretAction);

        break;
      }
    }

    this.movementDirection = movementDirection;
  }

  @Override
  public void setTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    if (this.movementDirection != MovementDirection.NONE || this.turnDirection != TurnDirection.NONE) return;

    switch (turnDirection)
    {
      case RIGHT:
      {
        bodyActor.addAction (Actions.sequence (Actions.rotateBy (- 90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (- 90);
          }
        })));

        break;
      }
      case LEFT:
      {
        bodyActor.addAction (Actions.sequence (Actions.rotateBy (90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (90);
          }
        })));

        break;
      }
      case U_TURN:
      {
        bodyActor.addAction (Actions.sequence (Actions.rotateBy (- 180, 2), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turnDirection = TurnDirection.NONE;

            bodyForwardVector.rotate (- 180);
          }
        })));

        break;
      }
    }

    this.turnDirection = turnDirection;
  }

  public void setTurretTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    if (this.turretTurnDirection != TurnDirection.NONE) return;

    switch (turnDirection)
    {
      case RIGHT:
      {
        turretActor.addAction (Actions.sequence (Actions.rotateBy (- 90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (- 90);
          }
        })));

        break;
      }
      case LEFT:
      {
        turretActor.addAction (Actions.sequence (Actions.rotateBy (90, 1), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (90);
          }
        })));

        break;
      }
      case U_TURN:
      {
        turretActor.addAction (Actions.sequence (Actions.rotateBy (- 180, 2), Actions.run (new Runnable ()
        {
          @Override
          public void run ()
          {
            TankActor2.this.turretTurnDirection = TurnDirection.NONE;

            turretForwardVector.rotate (- 180);
          }
        })));

        break;
      }
    }

    this.turretTurnDirection = turnDirection;
  }

  public void deactivateTurret ()
  {
    if (! turretActive) return;

    final int degrees = Math.round (turretForwardVector.angle (bodyForwardVector));

    if (degrees == - 90)
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

  private void clampPosition ()
  {
    if (currentPosition.x < BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX;
    if (currentPosition.x > BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX;
    if (currentPosition.y < BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX;
    if (currentPosition.y > BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX;
  }
}
