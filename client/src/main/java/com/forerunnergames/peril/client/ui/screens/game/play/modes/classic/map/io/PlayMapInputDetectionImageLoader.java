package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.graphics.Pixmap;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapInputDetectionImageLoader
{
  Pixmap load (final MapMetadata mapMetadata);
  void unload (final MapMetadata mapMetadata);
}
