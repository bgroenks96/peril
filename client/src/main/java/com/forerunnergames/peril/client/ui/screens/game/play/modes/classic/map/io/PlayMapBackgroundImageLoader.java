package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.core.shared.map.MapMetadata;

public interface PlayMapBackgroundImageLoader
{
  Image load (final MapMetadata mapMetadata);
  void unload (final MapMetadata mapMetadata);
}
