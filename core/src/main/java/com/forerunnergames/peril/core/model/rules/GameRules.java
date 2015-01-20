package com.forerunnergames.peril.core.model.rules;

public interface GameRules
{
  public int getInitialArmies ();

  public InitialCountryAssignment getInitialCountryAssignment ();

  public int getMinArmiesInHand ();

  public int getMaxArmiesInHand ();

  public int getMinPlayerLimit ();

  public int getMaxPlayerLimit ();

  public int getMinPlayers ();

  public int getMaxPlayers ();

  public int getMinTotalCountryCount ();

  public int getMaxTotalCountryCount ();

  public int getMinWinPercentage ();

  public int getMaxWinPercentage ();

  public int getPlayerLimit ();

  public int getTotalCountryCount ();

  public int getWinPercentage ();

  public int getWinningCountryCount ();

  public boolean isValidWinPercentage (final int winPercentage);
}
