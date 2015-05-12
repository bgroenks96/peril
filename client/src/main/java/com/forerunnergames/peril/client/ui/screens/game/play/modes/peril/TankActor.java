package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;

public final class TankActor extends Actor implements UnitActor
{
  private static final int SPEED_IN_GRID_SQUARES_PER_SECOND = 2;
  private static final float ACTION_THRESHOLD_SECONDS = 1.0f / SPEED_IN_GRID_SQUARES_PER_SECOND;
  private final Sprite bodySprite;
  private final Sprite turretSprite;
  private Vector2 spritePosition = new Vector2 (0, 0);
  private Vector2 bodyForwardVector = new Vector2 (0, 1);
  private Vector2 turretForwardVector = new Vector2 (0, 1);
  private Vector2 previousPosition = new Vector2 (0, 0);
  private Vector2 currentPosition = new Vector2 (0, 0);
  private MovementDirection movementDirection = MovementDirection.NONE;
  private TurnDirection turnDirection = TurnDirection.NONE;
  private TurnDirection turretTurnDirection = TurnDirection.NONE;
  private float timeSinceLastActionSeconds = 0.0f;
  private boolean completedTurningAround = false;
  private boolean completedTurningTurretAround = false;
  private boolean turretActive = false;

  public TankActor ()
  {
    bodySprite = Assets.perilModeAtlas.createSprite ("tankBody");
    turretSprite = Assets.perilModeAtlas.createSprite ("tankTurret");
    bodySprite.setOrigin (bodySprite.getWidth () / 2.0f, 22);
    turretSprite.setOrigin (turretSprite.getWidth () / 2.0f, 22);
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
  public void act (float delta)
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
          setTurning (UnitActor.TurnDirection.NONE);
          break;
        }
        case NONE:
        {
          setTurning (UnitActor.TurnDirection.LEFT);
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
          setTurning (UnitActor.TurnDirection.NONE);
          break;
        }
        case NONE:
        {
          setTurning (UnitActor.TurnDirection.RIGHT);
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
        if (! isTurning ()) setTurning (UnitActor.TurnDirection.U_TURN);
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
          setMoving (UnitActor.MovementDirection.NONE);
          break;
        }
        case NONE:
        {
          setMoving (UnitActor.MovementDirection.FORWARD);
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
          setMoving (UnitActor.MovementDirection.NONE);
          break;
        }
        case NONE:
        {
          setMoving (UnitActor.MovementDirection.REVERSE);
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
  public void turnRight ()
  {
    bodySprite.rotate (- 90);

    if (! turretActive)
    {
      turretSprite.rotate (- 90);
      turretForwardVector.rotate90 (1);
    }

    bodyForwardVector.rotate90 (1);

    turnDirection = TurnDirection.NONE;
  }

  @Override
  public void turnLeft ()
  {
    bodySprite.rotate (90);
    bodyForwardVector.rotate90 (- 1);

    if (! turretActive)
    {
      turretSprite.rotate (90);
      turretForwardVector.rotate90 (- 1);
    }

    turnDirection = TurnDirection.NONE;
  }

  @Override
  public void turnAround ()
  {
    bodySprite.rotate (- 90);
    bodyForwardVector.rotate90 (1);

    if (! turretActive)
    {
      turretSprite.rotate (- 90);
      turretForwardVector.rotate90 (1);
    }

    if (completedTurningAround) turnDirection = TurnDirection.NONE;

    completedTurningAround = ! completedTurningAround;
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

    this.movementDirection = movementDirection;
  }

  @Override
  public void setTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    this.turnDirection = turnDirection;
  }

  public void turnTurretRight ()
  {
    turretSprite.rotate (- 90);
    turretForwardVector.rotate90 (1);

    turretTurnDirection = TurnDirection.NONE;
    turretActive = true;
  }

  public void turnTurretLeft ()
  {
    turretSprite.rotate (90);
    turretForwardVector.rotate90 (- 1);

    turretTurnDirection = TurnDirection.NONE;
    turretActive = true;
  }

  public void turnTurretAround ()
  {
    turretSprite.rotate (- 90);
    turretForwardVector.rotate90 (1);

    if (completedTurningTurretAround) turretTurnDirection = TurnDirection.NONE;

    completedTurningTurretAround = ! completedTurningTurretAround;
    turretActive = true;
  }

  public void setTurretTurning (final TurnDirection turnDirection)
  {
    Arguments.checkIsNotNull (turnDirection, "turnDirection");

    turretTurnDirection = turnDirection;
  }

  public void deactivateTurret ()
  {
    if (! turretActive) return;

    final int degrees = (int) turretForwardVector.angle (bodyForwardVector);

    if (degrees == 0) return;

    if (degrees == 90)
    {
      setTurretTurning (TurnDirection.RIGHT);
    }
    else if (degrees == - 90)
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

  private boolean isMoving ()
  {
    return movementDirection != MovementDirection.NONE;
  }

  private boolean isTurning ()
  {
    return turnDirection != TurnDirection.NONE;
  }

  private boolean isTurretTurning ()
  {
    return turretTurnDirection != TurnDirection.NONE;
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
    if (currentPosition.x < BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX;
    if (currentPosition.x > BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX;
    if (currentPosition.y < BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX;
    if (currentPosition.y > BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX;
  }
}
