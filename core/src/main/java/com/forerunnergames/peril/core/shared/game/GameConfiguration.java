package com.forerunnergames.peril.core.shared.game;

import com.forerunnergames.peril.core.shared.map.MapMetadata;

public interface GameConfiguration
{
  GameMode getGameMode ();

  int getPlayerLimit ();

  int getWinPercentage ();

  InitialCountryAssignment getInitialCountryAssignment ();

  String getMapName ();

  MapMetadata getMapMetadata ();
}
