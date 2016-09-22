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

package com.forerunnergames.peril.core.model.turn;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

/**
 * Model class for tracking player turn.
 */
public final class DefaultPlayerTurnModel implements PlayerTurnModel
{
  private static final int FIRST = 0;
  private int turnCount;
  private int turn = FIRST;

  /**
   * @param turnCount
   *          the number of turns in this PlayerTurnModel's turn cycle
   */
  public DefaultPlayerTurnModel (final int turnCount)
  {
    Arguments.checkIsNotNegative (turnCount, "turnCount");
    Arguments.checkUpperInclusiveBound (turnCount, PlayerTurnOrder.validCount (), "turnCount");

    this.turnCount = turnCount;
  }

  @Override
  public void advance ()
  {
    turn = getNextTurnValue ();
  }

  @Override
  public int getTurn ()
  {
    return turn;
  }

  /**
   * @return the PlayerTurnOrder value corresponding to the current turn.
   */
  @Override
  public PlayerTurnOrder getTurnOrder ()
  {
    return PlayerTurnOrder.getNthValidTurnOrder (turn + 1);
  }

  @Override
  public boolean isFirstTurn ()
  {
    return getTurnOrder ().is (PlayerTurnOrder.FIRST);
  }

  @Override
  public boolean isLastTurn ()
  {
    return turn == turnCount - 1;
  }

  @Override
  public void reset ()
  {
    turn = FIRST;
  }

  @Override
  public int getTurnCount ()
  {
    return turnCount;
  }

  @Override
  public void setTurnCount (final int newTurnCount)
  {
    Arguments.checkIsNotNegative (newTurnCount, "newTurnCount");
    Arguments.checkUpperInclusiveBound (newTurnCount, PlayerTurnOrder.validCount (), "newTurnCount");

    turnCount = newTurnCount;

    if (newTurnCount >= turn) reset ();
  }

  private int getNextTurnValue ()
  {
    return (turn + 1) % turnCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: At turn {} / {}", getClass ().getSimpleName (), turn + 1, turnCount);
  }
}
