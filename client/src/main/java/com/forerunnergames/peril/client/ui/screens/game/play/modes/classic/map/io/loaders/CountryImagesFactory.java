package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImages;
import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface CountryImagesFactory
{
  void create (final MapMetadata mapMetadata, final ImmutableList <TextureAtlas> countryAtlases);

  ImmutableMap <String, CountryPrimaryImages> getPrimary (final MapMetadata mapMetadata);

  ImmutableMap <String, CountrySecondaryImages> getSecondary (final MapMetadata mapMetadata);

  void destroy ();
}
