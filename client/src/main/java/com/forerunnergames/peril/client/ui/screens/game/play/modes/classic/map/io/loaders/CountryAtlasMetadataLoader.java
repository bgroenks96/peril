package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryAtlasMetadata;
import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableSet;

public interface CountryAtlasMetadataLoader
{
  ImmutableSet <CountryAtlasMetadata> load (final MapMetadata mapMetadata);
}
