package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapInputDetectionFactory
{
  void loadAssets (MapMetadata mapMetadata);

  boolean isFinishedLoadingAssets (MapMetadata mapMetadata);

  PlayMapInputDetection create (MapMetadata mapMetadata);

  void destroy (MapMetadata mapMetadata);
}
