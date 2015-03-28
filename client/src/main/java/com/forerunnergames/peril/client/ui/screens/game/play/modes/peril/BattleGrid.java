package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;

public final class BattleGrid extends Table implements InputProcessor
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

    stack (gridLinesTable, unitActorTable);

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
    return unitActor.keyDown (keycode);
  }

  @Override
  public boolean keyUp (final int keycode)
  {
    return unitActor.keyUp (keycode);
  }

  @Override
  public boolean keyTyped (final char character)
  {
    return unitActor.keyTyped (character);
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    return unitActor.touchDown (screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return unitActor.touchUp (screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchDragged (final int screenX, final int screenY, final int pointer)
  {
    return unitActor.touchDragged (screenX, screenY, pointer);
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    return unitActor.mouseMoved (screenX, screenY);
  }

  @Override
  public boolean scrolled (final int amount)
  {
    return unitActor.scrolled (amount);
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
