package com.forerunnergames.peril.core.model.strategy;

import com.forerunnergames.tools.common.Arguments;

public final class DefaultGameStrategy implements GameStrategy
{
  // @formatter:off
  /**
   * Defined in DefaultGameStrategy by the following piecewise function:
   *
   * P(n) = | 1               if n = 10
   *        | 40 - 5*(n - 2)  if n < 10
   *
   * where 'P' is the number of armies returned in the set and 'n' is the number
   * of players in the given PlayerModel.
   */
  // @formatter:on
  @Override
  public int computeInitialArmyCount (final int playerCount)
  {
    Arguments.checkIsNotNegative (playerCount, "playerCount");

    return playerCount < 10 ? 40 - 5 * (playerCount - 2) : 5;
  }
}
