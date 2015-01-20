package com.forerunnergames.peril.core.model.rules;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class DefaultGameConfiguration implements GameConfiguration
{
  private final GameMode gameMode;
  private final int playerLimit;
  private final int winPercentage;
  private final int totalCountryCount;
  private final InitialCountryAssignment initialCountryAssignment;

  public DefaultGameConfiguration (final GameMode gameMode,
                                   final int playerLimit,
                                   final int winPercentage,
                                   final int totalCountryCount,
                                   final InitialCountryAssignment initialCountryAssignment)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");
    Arguments.checkIsNotNegative (winPercentage, "winPercentage");
    Arguments.checkIsNotNegative (totalCountryCount, "totalCountryCount");
    Arguments.checkIsNotNull (initialCountryAssignment, "initialCountryAssignment");

    this.gameMode = gameMode;
    this.playerLimit = playerLimit;
    this.winPercentage = winPercentage;
    this.totalCountryCount = totalCountryCount;
    this.initialCountryAssignment = initialCountryAssignment;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameMode;
  }

  @Override
  public int getPlayerLimit ()
  {
    return playerLimit;
  }

  @Override
  public int getWinPercentage ()
  {
    return winPercentage;
  }

  @Override
  public int getTotalCountryCount ()
  {
    return totalCountryCount;
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return initialCountryAssignment;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game mode: %2$s | Player limit: %3$s | Win Percentage: %4$s%"
                    + " | Total Country Count: %5$s" + " | Initial Country Assignment: %6$s", getClass ()
                    .getSimpleName (), gameMode, playerLimit, winPercentage, totalCountryCount,
                    initialCountryAssignment);
  }

  @RequiredForNetworkSerialization
  private DefaultGameConfiguration ()
  {
    gameMode = null;
    playerLimit = 0;
    winPercentage = 0;
    totalCountryCount = 0;
    initialCountryAssignment = null;
  }
}
