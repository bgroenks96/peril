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

package com.forerunnergames.peril.core.model.turn;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

/**
 * Model class for tracking player turn.
 */
public interface PlayerTurnModel
{
  public static final int DEFAULT_START_ROUND = 1;

  void advance ();

  PlayerTurnOrder getCurrentTurn ();

  PlayerTurnOrder getLastTurn ();

  boolean isFirstTurn ();

  boolean isLastTurn ();

  void decrementTurnCount ();

  void resetCurrentTurn ();

  void resetTurnCount ();

  void resetRound ();

  void resetAll ();

  int getRound ();

  boolean isRoundIncreasing ();

  void setRoundIncreasing (boolean shouldRoundsIncrease);

  @Override
  String toString ();
}
