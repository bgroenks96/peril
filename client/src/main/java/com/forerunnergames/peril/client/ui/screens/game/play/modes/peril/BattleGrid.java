/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;

public final class BattleGrid extends Table implements InputProcessor
{
  private final Unit unit;
  private final Table unitTable;

  public BattleGrid (final Image gridLinesImage, final Unit unit)
  {
    Arguments.checkIsNotNull (gridLinesImage, "gridLinesImage");
    Arguments.checkIsNotNull (unit, "unit");

    this.unit = unit;

    final Table gridLinesTable = new Table ();

    unitTable = new Table ();

    for (int row = 0; row < BattleGridSettings.BATTLE_GRID_ROW_COUNT; ++row)
    {
      for (int column = 0; column < BattleGridSettings.BATTLE_GRID_COLUMN_COUNT; ++column)
      {
        gridLinesTable.add (gridLinesImage).width (BattleGridSettings.BATTLE_GRID_CELL_WIDTH)
                .height (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT);
        unitTable.add ().width (BattleGridSettings.BATTLE_GRID_CELL_WIDTH)
                .height (BattleGridSettings.BATTLE_GRID_CELL_HEIGHT);
      }

      gridLinesTable.row ();
      unitTable.row ();
    }

    stack (gridLinesTable, unitTable);

    gridLinesTable.setVisible (BattleGridSettings.VISIBLE_GRID_LINES);

    updateGridPositionOf (unit);
  }

  @Override
  public void act (final float delta)
  {
    super.act (delta);

    if (shouldUpdateGridPositionOf (unit)) updateGridPositionOf (unit);
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    return unit.keyDown (keycode);
  }

  @Override
  public boolean keyUp (final int keycode)
  {
    return unit.keyUp (keycode);
  }

  @Override
  public boolean keyTyped (final char character)
  {
    return unit.keyTyped (character);
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    return unit.touchDown (screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return unit.touchUp (screenX, screenY, pointer, button);
  }

  @Override
  public boolean touchDragged (final int screenX, final int screenY, final int pointer)
  {
    return unit.touchDragged (screenX, screenY, pointer);
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    return unit.mouseMoved (screenX, screenY);
  }

  @Override
  public boolean scrolled (final int amount)
  {
    return unit.scrolled (amount);
  }

  private static boolean areEqual (final Cell <?> cell1, final Cell <?> cell2)
  {
    return cell1 != null && cell2 != null && cell1.getRow () == cell2.getRow ()
            && cell1.getColumn () == cell2.getColumn ();
  }

  private static void removeUnitFrom (final Cell <?> cell)
  {
    setCellActor (cell, null);
  }

  private static int gridPositionToCellIndex (final Vector2 gridPosition)
  {
    return Math.round (gridPosition.y * BattleGridSettings.BATTLE_GRID_COLUMN_COUNT + gridPosition.x);
  }

  private static Vector2 previousGridPositionOf (final Unit unit)
  {
    return unit.getPreviousPosition ();
  }

  private static void addUnitTo (final Cell <?> cell, final Unit unit)
  {
    setCellActor (cell, unit.asActor ());
  }

  private static void setCellActor (final Cell <?> cell, final Actor actor)
  {
    cell.setActor (actor);
  }

  private static Vector2 currentGridPositionOf (final Unit unit)
  {
    return unit.getCurrentPosition ();
  }

  private boolean shouldUpdateGridPositionOf (final Unit unit)
  {
    return !areEqual (previousCellOf (unit), currentCellOf (unit));
  }

  private void updateGridPositionOf (final Unit unit)
  {
    removeUnitFrom (previousCellOf (unit));
    addUnitTo (currentCellOf (unit), unit);
  }

  private Cell <?> previousCellOf (final Unit unit)
  {
    return cellAt (gridPositionToCellIndex (previousGridPositionOf (unit)));
  }

  private Cell <?> cellAt (final int cellIndex)
  {
    return (Cell <?>) unitTable.getCells ().get (cellIndex);
  }

  private Cell <?> currentCellOf (final Unit unit)
  {
    return cellAt (gridPositionToCellIndex (currentGridPositionOf (unit)));
  }
}
