package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface PlayMapBackgroundImageLoader
{
  void load (final MapMetadata mapMetadata);

  boolean isFinishedLoading (final MapMetadata mapMetadata);

  Image get (final MapMetadata mapMetadata);

  void unload (final MapMetadata mapMetadata);
}
