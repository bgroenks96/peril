package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryAtlasesLoader implements CountryAtlasesLoader
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryAtlasesLoader.class);
  private final Multimap <MapMetadata, CountryAtlasMetadata> loadedCountryAtlases = HashMultimap.create ();
  private final AssetManager assetManager;

  public DefaultCountryAtlasesLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public ImmutableList <TextureAtlas> load (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    final CountryAtlasMetadataLoader countryAtlasMetadataLoader = new DefaultCountryAtlasMetadataLoader (mapMetadata);
    final ImmutableSet <CountryAtlasMetadata> countryAtlasMetadatas = countryAtlasMetadataLoader.load ();

    for (final CountryAtlasMetadata countryAtlasMetadata : countryAtlasMetadatas)
    {
      assetManager.load (countryAtlasMetadata.getAssetDescriptor ());
    }

    assetManager.finishLoading ();

    final ImmutableList.Builder <TextureAtlas> countryAtlasesBuilder = ImmutableList.builder ();

    for (final CountryAtlasMetadata countryAtlasMetadata : countryAtlasMetadatas)
    {
      loadedCountryAtlases.put (mapMetadata, countryAtlasMetadata);
      countryAtlasesBuilder.add (assetManager.get (countryAtlasMetadata.getAssetDescriptor ()));
    }

    return countryAtlasesBuilder.build ();
  }

  @Override
  public void unload (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (!loadedCountryAtlases.containsKey (mapMetadata))
    {
      log.warn ("Cannot unload country atlases for map [{}] because it is not loaded.", mapMetadata);
      return;
    }

    for (final CountryAtlasMetadata countryAtlasMetadata : loadedCountryAtlases.get (mapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getFileName ()))
      {
        log.warn ("Cannot unload country atlas [{}] for map [{}] because it is not loaded.", countryAtlasMetadata,
                  mapMetadata);
        continue;
      }

      assetManager.unload (countryAtlasMetadata.getFileName ());
    }
  }
}
