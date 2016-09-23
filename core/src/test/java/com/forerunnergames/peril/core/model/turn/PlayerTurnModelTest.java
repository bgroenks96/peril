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

import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

import org.junit.Before;
import org.junit.Test;

public class PlayerTurnModelTest
{
  private static final int PLAYER_LIMIT = ClassicGameRules.MAX_PLAYER_LIMIT;
  private static final int TURN_COUNT = PLAYER_LIMIT;
  private static final PlayerTurnOrder LAST_TURN = PlayerTurnOrder.getNthValidTurnOrder (TURN_COUNT);
  private static final GameRules RULES = new ClassicGameRules.Builder ().playerLimit (PLAYER_LIMIT).build ();
  private PlayerTurnModel turnModel;

  @Before
  public void beforeTest ()
  {
    turnModel = new DefaultPlayerTurnModel (RULES);
  }

  @Test
  public void testStartsAtFirstTurn ()
  {
    assertTrue (turnModel.isFirstTurn ());
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getCurrentTurn ());
  }

  @Test
  public void testAdvanceFromFirstToSecondTurn ()
  {
    turnModel.advance ();
    assertEquals (PlayerTurnOrder.SECOND, turnModel.getCurrentTurn ());
  }

  @Test
  public void testAdvanceFromLastToFirstTurn ()
  {
    advanceToLastTurn ();
    turnModel.advance ();
    assertTrue (turnModel.isFirstTurn ());
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getCurrentTurn ());
  }

  @Test
  public void testGetTurnOrderAtLastTurn ()
  {
    advanceToLastTurn ();
    assertTrue (turnModel.isLastTurn ());
    assertEquals (LAST_TURN, turnModel.getCurrentTurn ());
  }

  @Test
  public void testDecrementTurnCount ()
  {
    turnModel.decrementTurnCount ();
    assertEquals (LAST_TURN.previousValid (), turnModel.getLastTurn ());
  }

  @Test
  public void testDecrementTurnCountAtLastTurn ()
  {
    advanceToLastTurn ();
    turnModel.decrementTurnCount ();
    assertTrue (turnModel.isFirstTurn ());
  }

  @Test
  public void testDecrementTurnCountAtFirstTurn ()
  {
    turnModel.decrementTurnCount ();
    assertTrue (turnModel.isFirstTurn ());
  }

  @Test
  public void testDecrementTurnCountAtSecondTurn ()
  {
    turnModel.advance ();
    turnModel.decrementTurnCount ();
    assertEquals (PlayerTurnOrder.SECOND, turnModel.getCurrentTurn ());
  }

  @Test
  public void testDecrementTurnCountAtSecondToLastTurn ()
  {
    advanceToTurn (LAST_TURN.previousValid ());
    turnModel.decrementTurnCount ();
    assertEquals (LAST_TURN.previousValid (), turnModel.getCurrentTurn ());
  }

  @Test
  public void testDecrementTurnCountTwiceAtSecondToLastTurn ()
  {
    advanceToTurn (LAST_TURN.previousValid ());
    turnModel.decrementTurnCount ();
    turnModel.decrementTurnCount ();
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getCurrentTurn ());
  }

  @Test
  public void testResetCurrentTurnFromLastTurn ()
  {
    advanceToLastTurn ();
    turnModel.resetCurrentTurn ();
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getCurrentTurn ());
  }

  @Test
  public void testResetCurrentTurnFromFirstTurn ()
  {
    turnModel.resetCurrentTurn ();
    assertEquals (PlayerTurnOrder.FIRST, turnModel.getCurrentTurn ());
  }

  @Test
  public void testResetTurnCount ()
  {
    turnModel.resetTurnCount ();
    assertEquals (LAST_TURN, turnModel.getLastTurn ());
  }

  @Test
  public void testResetTurnCountAfterDecrement ()
  {
    turnModel.decrementTurnCount ();
    turnModel.resetTurnCount ();
    assertEquals (LAST_TURN, turnModel.getLastTurn ());
  }

  @Test
  public void testResetTurnCountAfterDecrementTwice ()
  {
    turnModel.decrementTurnCount ();
    turnModel.decrementTurnCount ();
    turnModel.resetTurnCount ();
    assertEquals (LAST_TURN, turnModel.getLastTurn ());
  }

  @Test
  public void testGetLastTurn ()
  {
    assertEquals (LAST_TURN, turnModel.getLastTurn ());
  }

  @Test
  public void testGetLastTurnAfterDecrement ()
  {
    turnModel.decrementTurnCount ();
    assertEquals (LAST_TURN.previousValid (), turnModel.getLastTurn ());
  }

  private void advanceToTurn (final PlayerTurnOrder turn)
  {
    for (int i = 0; i < turn.getPosition () - 1; i++)
    {
      turnModel.advance ();
    }

    assertEquals (turn, turnModel.getCurrentTurn ());
  }

  private void advanceToLastTurn ()
  {
    advanceToTurn (LAST_TURN);
  }
}
