package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableList;

public interface CountryAtlasesLoader
{
  ImmutableList <TextureAtlas> load (final MapMetadata mapMetadata);
  void unload (final MapMetadata mapMetadata);
}
