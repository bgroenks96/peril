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
  public void reset ()
  {
    turn = FIRST;
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
  public int getTurn ()
  {
    return turn;
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

  @Override
  public String toString ()
  {
    return Strings.format ("{}: At turn {} / {}", getClass ().getSimpleName (), turn + 1, turnCount);
  }

  private int getNextTurnValue ()
  {
    return (turn + 1) % turnCount;
  }
}
