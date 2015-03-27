package com.forerunnergames.peril.client.ui.screens.game.play.modes.peril;

import com.forerunnergames.tools.common.Classes;

public final class BattleGridSettings
{
  public static final int BATTLE_GRID_ROW_COUNT = 15;
  public static final int BATTLE_GRID_COLUMN_COUNT = 15;
  public static final int BATTLE_GRID_CELL_WIDTH = 70;
  public static final int BATTLE_GRID_CELL_HEIGHT = 70;
  public static final int BATTLE_GRID_COLUMN_MIN_INDEX = 0;
  public static final int BATTLE_GRID_COLUMN_MAX_INDEX = BATTLE_GRID_COLUMN_COUNT - 1;
  public static final int BATTLE_GRID_ROW_MIN_INDEX = 0;
  public static final int BATTLE_GRID_ROW_MAX_INDEX = BATTLE_GRID_ROW_COUNT - 1;
  public static final boolean CONTINUOUS_UNIT_MOVEMENT = false;
  public static final boolean VISIBLE_GRID_LINES = true;

  private BattleGridSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
