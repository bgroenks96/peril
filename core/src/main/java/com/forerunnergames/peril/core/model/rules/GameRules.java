package com.forerunnergames.peril.core.model.rules;

public interface GameRules
{
  int getInitialArmies ();

  InitialCountryAssignment getInitialCountryAssignment ();

  int getMinArmiesInHand ();

  int getMaxArmiesInHand ();

  int getMinPlayerLimit ();

  int getMaxPlayerLimit ();

  int getMinPlayers ();

  int getMaxPlayers ();

  int getMinTotalCountryCount ();

  int getMaxTotalCountryCount ();

  int getMinWinPercentage ();

  int getMaxWinPercentage ();

  int getPlayerLimit ();

  int getTotalCountryCount ();

  int getWinPercentage ();

  int getWinningCountryCount ();

  boolean isValidWinPercentage (final int winPercentage);
}
