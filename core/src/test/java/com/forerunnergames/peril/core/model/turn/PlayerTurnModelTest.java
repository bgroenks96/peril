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
