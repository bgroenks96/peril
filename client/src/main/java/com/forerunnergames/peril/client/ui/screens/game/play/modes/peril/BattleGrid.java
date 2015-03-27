package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;

public final class BattleGrid extends Stack implements InputProcessor
{
  private final UnitActor unitActor;
  private Table unitActorTable;

  public BattleGrid (final UnitActor unitActor)
  {
    Arguments.checkIsNotNull (unitActor, "unitActor");

    this.unitActor = unitActor;

    final Table gridLinesTable = new Table ();

    unitActorTable = new Table ();

    for (int row = 0; row < BattleGridSettings.BATTLE_GRID_ROW_COUNT; ++row)
    {
      for (int column = 0; column < BattleGridSettings.BATTLE_GRID_COLUMN_COUNT; ++column)
      {
        gridLinesTable.add (new Image (Assets.perilModeGridLines)).width (BattleGridSettings.BATTLE_GRID_CELL_WIDTH)
            .height (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT);
        unitActorTable.add ().width (BattleGridSettings.BATTLE_GRID_CELL_WIDTH)
            .height (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT);
      }

      gridLinesTable.row ();
      unitActorTable.row ();
    }

    add (gridLinesTable);
    add (unitActorTable);

    gridLinesTable.setVisible (BattleGridSettings.VISIBLE_GRID_LINES);

    updateGridPositionOf (unitActor);
  }

  @Override
  public void act (float delta)
  {
    super.act (delta);

    if (shouldUpdateGridPositionOf (unitActor)) updateGridPositionOf (unitActor);
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
        unitActor.setMoving (UnitActor.MovementDirection.LEFT);
      }
      else
      {
        unitActor.moveLeft ();
      }

      return true;
    }
    case Input.Keys.RIGHT:
    {
      if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
      {
        unitActor.setMoving (UnitActor.MovementDirection.RIGHT);
      }
      else
      {
        unitActor.moveRight ();
      }

      return true;
    }
    case Input.Keys.UP:
    {
      if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
      {
        unitActor.setMoving (UnitActor.MovementDirection.UP);
      }
      else
      {
        unitActor.moveUp ();
      }

      return true;
    }
    case Input.Keys.DOWN:
    {
      if (BattleGridSettings.CONTINUOUS_UNIT_MOVEMENT)
      {
        unitActor.setMoving (UnitActor.MovementDirection.DOWN);
      }
      else
      {
        unitActor.moveDown ();
      }

      return true;
    }
    default:
    {
      return true;
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

  private boolean shouldUpdateGridPositionOf (final UnitActor unitActor)
  {
    return ! areEqual (previousCellOf (unitActor), currentCellOf (unitActor));
  }

  private boolean areEqual (final Cell <?> cell1, final Cell <?> cell2)
  {
    return cell1 != null && cell2 != null && cell1.getRow () == cell2.getRow ()
        && cell1.getColumn () == cell2.getColumn ();
  }

  private void updateGridPositionOf (final UnitActor unitActor)
  {
    removeUnitActorFrom (previousCellOf (unitActor));
    addUnitActorTo (currentCellOf (unitActor), unitActor);
  }

  private void removeUnitActorFrom (final Cell <?> cell)
  {
    setCellActor (cell, null);
  }

  private Cell <?> previousCellOf (final UnitActor unitActor)
  {
    return cellAt (gridPositionToCellIndex (previousGridPositionOf (unitActor)));
  }

  private Cell <?> cellAt (final int cellIndex)
  {
    return (Cell <?>) unitActorTable.getCells ().get (cellIndex);
  }

  private int gridPositionToCellIndex (final Vector2 gridPosition)
  {
    return (int) (gridPosition.y * BattleGridSettings.BATTLE_GRID_COLUMN_COUNT + gridPosition.x);
  }

  private Vector2 previousGridPositionOf (final UnitActor unitActor)
  {
    return unitActor.getPreviousPosition ();
  }

  private void addUnitActorTo (final Cell <?> cell, final UnitActor unitActor)
  {
    setCellActor (cell, unitActor.asActor ());
  }

  private void setCellActor (final Cell <?> cell, final Actor actor)
  {
    cell.setActor (actor);
  }

  private Cell <?> currentCellOf (final UnitActor unitActor)
  {
    return cellAt (gridPositionToCellIndex (currentGridPositionOf (unitActor)));
  }

  private Vector2 currentGridPositionOf (final UnitActor unitActor)
  {
    return unitActor.getCurrentPosition ();
  }
}
