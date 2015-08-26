package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapActorFactory
{
  void loadAssets (MapMetadata mapMetadata);

  boolean isFinishedLoadingAssets (MapMetadata mapMetadata);

  PlayMapActor create (MapMetadata mapMetadata);

  void destroy (MapMetadata mapMetadata);

  float getAssetLoadingProgressPercent (final MapMetadata mapMetadata);
}
