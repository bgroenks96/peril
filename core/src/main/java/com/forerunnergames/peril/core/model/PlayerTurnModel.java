package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Arguments;

/**
 * Model class for tracking player turn.
 */
public final class PlayerTurnModel
{
  private static final int FIRST = 0;
  private int turnCount;
  private int turn = FIRST;

  /**
   * @param turnCount
   *          the number of turns in this PlayerTurnModel's turn cycle
   */
  public PlayerTurnModel (final int turnCount)
  {
    Arguments.checkIsNotNegative (turnCount, "turnCount");
    Arguments.checkUpperInclusiveBound (turnCount, PlayerTurnOrder.validCount (), "turnCount");

    this.turnCount = turnCount;
  }

  public void advance ()
  {
    turn = (turn + 1) % turnCount;
  }

  public void reset ()
  {
    turn = FIRST;
  }

  /**
   * @return the PlayerTurnOrder value corresponding to the current turn.
   */
  public PlayerTurnOrder getTurnOrder ()
  {
    return PlayerTurnOrder.getNthValidTurnOrder (turn + 1);
  }

  public int getTurn ()
  {
    return turn;
  }

  public boolean isFirstTurn ()
  {
    return getTurnOrder ().is (PlayerTurnOrder.FIRST);
  }

  public boolean isLastTurn ()
  {
    return getTurnOrder ().hasNextValid ();
  }

  public int getTurnCount ()
  {
    return turnCount;
  }

  public void setTurnCount (final int newTurnCount)
  {
    Arguments.checkIsNotNegative (newTurnCount, "newTurnCount");
    Arguments.checkUpperInclusiveBound (newTurnCount, PlayerTurnOrder.validCount (), "newTurnCount");

    this.turnCount = newTurnCount;

    if (newTurnCount >= turn) reset ();
  }
}
