package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface CountryAtlasMetadata
{
  AssetDescriptor <TextureAtlas> getAssetDescriptor ();

  MapMetadata getMapMetadata ();

  String getFileName ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}