package com.forerunnergames.peril.core.model.rules;

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
