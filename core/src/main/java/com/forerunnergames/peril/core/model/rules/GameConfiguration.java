package com.forerunnergames.peril.core.model.rules;

public interface GameConfiguration
{
  public GameMode getGameMode ();

  public int getPlayerLimit ();

  public int getWinPercentage ();

  public int getTotalCountryCount ();

  public InitialCountryAssignment getInitialCountryAssignment ();
}
