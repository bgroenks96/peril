package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;

public final class TankActor extends Actor implements UnitActor
{
  private static final int SPEED_IN_GRID_SQUARES_PER_SECOND = 2;
  private static final float MOVEMENT_THRESHOLD_SECONDS = 1.0f / SPEED_IN_GRID_SQUARES_PER_SECOND;
  private final Sprite bodySprite;
  private final Sprite turretSprite;
  private Vector2 position = new Vector2 (0, 0);
  private Vector2 horizontalMovementRate = new Vector2 (1, 0);
  private Vector2 verticalMovementRate = new Vector2 (0, 1);
  private Vector2 previousPosition = new Vector2 (0, 0);
  private Vector2 currentPosition = new Vector2 (0, 0);
  private MovementDirection movementDirection = MovementDirection.NONE;
  private FacingDirection facingDirection = FacingDirection.UP;
  private float timeSinceLastMovementSeconds = 0.0f;

  public TankActor ()
  {
    bodySprite = Assets.perilModeAtlas.createSprite ("tankBody");
    turretSprite = Assets.perilModeAtlas.createSprite ("tankTurret");
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    position.set ((BattleGridSettings.BATTLE_GRID_CELL_WIDTH - bodySprite.getWidth ()) / 2.0f,
                  (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT - bodySprite.getHeight ()) / 2.0f);
    localToParentCoordinates (position);
    bodySprite.setPosition (position.x, position.y);
    turretSprite.setPosition (position.x, position.y + 8);
    bodySprite.draw (batch);
    turretSprite.draw (batch);
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    timeSinceLastMovementSeconds += delta;

    if (timeSinceLastMovementSeconds < MOVEMENT_THRESHOLD_SECONDS) return;

    timeSinceLastMovementSeconds = 0.0f;

    switch (movementDirection)
    {
    case UP:
    {
      moveUp ();
      break;
    }
    case DOWN:
    {
      moveDown ();
      break;
    }
    case LEFT:
    {
      moveLeft ();
      break;
    }
    case RIGHT:
    {
      moveRight ();
      break;
    }
    default:
    {
      break;
    }
    }
  }

  @Override
  public void moveRight ()
  {
    setFacing (FacingDirection.RIGHT);

    previousPosition.set (currentPosition);
    currentPosition.add (horizontalMovementRate);

    if (currentPosition.x > BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MAX_INDEX;
  }

  @Override
  public void moveLeft ()
  {
    setFacing (FacingDirection.LEFT);

    previousPosition.set (currentPosition);
    currentPosition.sub (horizontalMovementRate);

    if (currentPosition.x < BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX) currentPosition.x = BattleGridSettings.BATTLE_GRID_COLUMN_MIN_INDEX;
  }

  @Override
  public void moveUp ()
  {
    setFacing (FacingDirection.UP);

    previousPosition.set (currentPosition);
    currentPosition.sub (verticalMovementRate);

    if (currentPosition.y < BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MIN_INDEX;
  }

  @Override
  public void moveDown ()
  {
    setFacing (FacingDirection.DOWN);

    previousPosition.set (currentPosition);
    currentPosition.add (verticalMovementRate);

    if (currentPosition.y > BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX) currentPosition.y = BattleGridSettings.BATTLE_GRID_ROW_MAX_INDEX;
  }

  @Override
  public void setMoving (final MovementDirection movementDirection)
  {
    Arguments.checkIsNotNull (movementDirection, "movementDirection");

    this.movementDirection = movementDirection;
  }

  @Override
  public void setFacing (final FacingDirection facingDirection)
  {
    Arguments.checkIsNotNull (facingDirection, "facingDirection");

    if (facingDirection == this.facingDirection) return;

    bodySprite.rotate (degreesBetween (this.facingDirection, facingDirection));

    this.facingDirection = facingDirection;
  }

  private int degreesBetween (final FacingDirection facingDirection1, final FacingDirection facingDirection2)
  {
    return facingDirection1.degrees () - facingDirection2.degrees ();
  }

  @Override
  public boolean isMoving ()
  {
    return movementDirection != MovementDirection.NONE;
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
  public boolean isNot (final Actor actor)
  {
    return ! is (actor);
  }
}
