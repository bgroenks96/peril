package com.forerunnergames.peril.core.model;

import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

import org.junit.Test;

public class PlayerTurnModelTest
{
  private static final int DEFAULT_TURN_COUNT = 3;

  @Test
  public void testStartsAtFirstTurn ()
  {
    final PlayerTurnModel turnModel = new PlayerTurnModel (DEFAULT_TURN_COUNT);
    assertTrue (turnModel.isFirstTurn ());
    assertTrue (turnModel.getTurn () == 0);
    assertTrue (turnModel.getTurnOrder () == PlayerTurnOrder.FIRST);
  }

  @Test
  public void testAdvanceFromFirstToSecondTurn ()
  {
    final PlayerTurnModel turnModel = new PlayerTurnModel (DEFAULT_TURN_COUNT);
    turnModel.advance ();
    assertTrue (turnModel.getTurn () == 1);
    assertTrue (turnModel.getTurnOrder () == PlayerTurnOrder.SECOND);
  }

  @Test
  public void testAdvanceFromLastToFirstTurn ()
  {
    final PlayerTurnModel turnModel = new PlayerTurnModel (DEFAULT_TURN_COUNT);
    for (int i = 0; i < DEFAULT_TURN_COUNT - 1; i++)
    {
      turnModel.advance ();
    }
    assertTrue (turnModel.isLastTurn ());

    turnModel.advance ();

    assertTrue (turnModel.isFirstTurn ());
    assertTrue (turnModel.getTurnOrder () == PlayerTurnOrder.FIRST);
  }
}
