package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapInputDetectionFactory
{
  void loadAssets (MapMetadata mapMetadata);

  boolean isFinishedLoadingAssets (MapMetadata mapMetadata);

  PlayMapInputDetection create (MapMetadata mapMetadata, final Vector2 playMapReferenceSize);

  void destroy (MapMetadata mapMetadata);
}
