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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

import org.junit.Test;

public class PlayerTurnModelTest
{
  private static final int DEFAULT_TURN_COUNT = 10;
  private static final PlayerTurnOrder DEFAULT_LAST_TURN = PlayerTurnOrder.TENTH;

  @Test
  public void testStartsAtFirstTurn ()
  {
    final PlayerTurnModel turnModel = new DefaultPlayerTurnModel (DEFAULT_TURN_COUNT);
    assertTrue (turnModel.isFirstTurn ());
    assertTrue (turnModel.getTurn () == 0);
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getTurnOrder ());
  }

  @Test
  public void testAdvanceFromFirstToSecondTurn ()
  {
    final PlayerTurnModel turnModel = new DefaultPlayerTurnModel (DEFAULT_TURN_COUNT);
    turnModel.advance ();
    assertTrue (turnModel.getTurn () == 1);
    assertEquals (PlayerTurnOrder.SECOND, turnModel.getTurnOrder ());
  }

  @Test
  public void testAdvanceFromLastToFirstTurn ()
  {
    final PlayerTurnModel turnModel = new DefaultPlayerTurnModel (DEFAULT_TURN_COUNT);
    for (int i = 0; i < DEFAULT_TURN_COUNT - 1; i++)
    {
      turnModel.advance ();
    }
    assertTrue (turnModel.isLastTurn ());

    turnModel.advance ();

    assertTrue (turnModel.isFirstTurn ());
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getTurnOrder ());
  }

  @Test
  public void testGetTurnOrderAtLastTurn ()
  {
    final PlayerTurnModel turnModel = new DefaultPlayerTurnModel (DEFAULT_TURN_COUNT);
    for (int i = 0; i < DEFAULT_TURN_COUNT - 1; i++)
    {
      turnModel.advance ();
    }
    assertTrue (turnModel.isLastTurn ());
    assertEquals (DEFAULT_LAST_TURN, turnModel.getTurnOrder ());
  }
}
