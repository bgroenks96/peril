package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;

public final class BattleGrid extends Table implements InputProcessor
{
  private final UnitActor unitActor;
  private final Table unitActorTable;

  public BattleGrid (final Image gridLinesImage, final UnitActor unitActor)
  {
    Arguments.checkIsNotNull (gridLinesImage, "gridLinesImage");
    Arguments.checkIsNotNull (unitActor, "unitActor");

    this.unitActor = unitActor;

    final Table gridLinesTable = new Table ();

    unitActorTable = new Table ();

    for (int row = 0; row < BattleGridSettings.BATTLE_GRID_ROW_COUNT; ++row)
    {
      for (int column = 0; column < BattleGridSettings.BATTLE_GRID_COLUMN_COUNT; ++column)
      {
        gridLinesTable.add (gridLinesImage).width (BattleGridSettings.BATTLE_GRID_CELL_WIDTH)
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
  public void act (final float delta)
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

  private static boolean areEqual (final Cell <?> cell1, final Cell <?> cell2)
  {
    return cell1 != null && cell2 != null && cell1.getRow () == cell2.getRow ()
            && cell1.getColumn () == cell2.getColumn ();
  }

  private static void removeUnitActorFrom (final Cell <?> cell)
  {
    setCellActor (cell, null);
  }

  private static int gridPositionToCellIndex (final Vector2 gridPosition)
  {
    return Math.round (gridPosition.y * BattleGridSettings.BATTLE_GRID_COLUMN_COUNT + gridPosition.x);
  }

  private static Vector2 previousGridPositionOf (final UnitActor unitActor)
  {
    return unitActor.getPreviousPosition ();
  }

  private static void addUnitActorTo (final Cell <?> cell, final UnitActor unitActor)
  {
    setCellActor (cell, unitActor.asActor ());
  }

  private static void setCellActor (final Cell <?> cell, final Actor actor)
  {
    cell.setActor (actor);
  }

  private static Vector2 currentGridPositionOf (final UnitActor unitActor)
  {
    return unitActor.getCurrentPosition ();
  }

  private boolean shouldUpdateGridPositionOf (final UnitActor unitActor)
  {
    return !areEqual (previousCellOf (unitActor), currentCellOf (unitActor));
  }

  private void updateGridPositionOf (final UnitActor unitActor)
  {
    removeUnitActorFrom (previousCellOf (unitActor));
    addUnitActorTo (currentCellOf (unitActor), unitActor);
  }

  private Cell <?> previousCellOf (final UnitActor unitActor)
  {
    return cellAt (gridPositionToCellIndex (previousGridPositionOf (unitActor)));
  }

  private Cell <?> cellAt (final int cellIndex)
  {
    return (Cell <?>) unitActorTable.getCells ().get (cellIndex);
  }

  private Cell <?> currentCellOf (final UnitActor unitActor)
  {
    return cellAt (gridPositionToCellIndex (currentGridPositionOf (unitActor)));
  }
}
