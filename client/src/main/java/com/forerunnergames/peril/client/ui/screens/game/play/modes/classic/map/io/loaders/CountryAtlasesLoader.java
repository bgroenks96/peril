package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableList;

public interface CountryAtlasesLoader
{
  void load (final MapMetadata mapMetadata);

  boolean isFinishedLoading (final MapMetadata mapMetadata);

  ImmutableList <TextureAtlas> get (final MapMetadata mapMetadata);

  void unload (final MapMetadata mapMetadata);
}
