package com.forerunnergames.peril.core.model.rules;

public interface GameConfiguration
{
  GameMode getGameMode ();

  int getPlayerLimit ();

  int getWinPercentage ();

  InitialCountryAssignment getInitialCountryAssignment ();
}
