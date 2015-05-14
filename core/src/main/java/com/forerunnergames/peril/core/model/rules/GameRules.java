package com.forerunnergames.peril.core.model.rules;

import com.google.common.collect.ImmutableList;

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

  ImmutableList <Integer> getInitialPlayerCountryDistribution (final int playerCount);

  boolean isValidWinPercentage (final int winPercentage);
}
