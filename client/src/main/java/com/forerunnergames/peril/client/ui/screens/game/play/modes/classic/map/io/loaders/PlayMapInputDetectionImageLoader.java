package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.graphics.Pixmap;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapInputDetectionImageLoader
{
  void load (final MapMetadata mapMetadata);

  boolean isFinishedLoading (final MapMetadata mapMetadata);

  Pixmap get (final MapMetadata mapMetadata);

  void unload (final MapMetadata mapMetadata);
}
