package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface GameConfiguration
{
  GameMode getGameMode ();

  int getPlayerLimit ();

  int getWinPercentage ();

  InitialCountryAssignment getInitialCountryAssignment ();

  String getMapName ();

  MapMetadata getMapMetadata ();
}
