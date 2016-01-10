package com.forerunnergames.peril.common.game;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;

public interface GameConfiguration
{
  GameMode getGameMode ();

  int getPlayerLimit ();

  int getSpectatorLimit ();

  int getWinPercentage ();

  InitialCountryAssignment getInitialCountryAssignment ();

  String getMapName ();

  MapMetadata getMapMetadata ();

  MapType getMapType ();
}
