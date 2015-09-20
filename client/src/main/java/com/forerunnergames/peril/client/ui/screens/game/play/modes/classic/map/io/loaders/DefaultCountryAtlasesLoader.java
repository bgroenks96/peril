package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryAtlasMetadata;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryAtlasesLoader implements CountryAtlasesLoader
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryAtlasesLoader.class);
  private final Multimap <MapMetadata, CountryAtlasMetadata> mapMetadataToCountryAtlasesMetadata = HashMultimap.create ();
  private final CountryAtlasMetadataLoader countryAtlasMetadataLoader = new DefaultCountryAtlasMetadataLoader ();
  private final AssetManager assetManager;
  // @formatter:on

  public DefaultCountryAtlasesLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void load (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (!mapMetadataToCountryAtlasesMetadata.containsKey (mapMetadata),
                               Strings.format ("Country atlases were already loaded for map [{}].", mapMetadata));

    for (final CountryAtlasMetadata countryAtlasMetadata : countryAtlasMetadataLoader.load (mapMetadata))
    {
      assetManager.load (countryAtlasMetadata.getAssetDescriptor ());
      mapMetadataToCountryAtlasesMetadata.put (mapMetadata, countryAtlasMetadata);
    }
  }

  @Override
  public boolean isFinishedLoading (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (mapMetadataToCountryAtlasesMetadata.containsKey (mapMetadata),
                               Strings.format ("Country atlases were never loaded for map [{}].", mapMetadata));

    for (final CountryAtlasMetadata countryAtlasMetadata : mapMetadataToCountryAtlasesMetadata.get (mapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ())) return false;
    }

    return true;
  }

  @Override
  public ImmutableList <TextureAtlas> get (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Preconditions.checkIsTrue (mapMetadataToCountryAtlasesMetadata.containsKey (mapMetadata),
                               Strings.format ("Country atlases were never loaded for map [{}].", mapMetadata));

    final ImmutableList.Builder <TextureAtlas> countryAtlasesBuilder = ImmutableList.builder ();

    for (final CountryAtlasMetadata countryAtlasMetadata : mapMetadataToCountryAtlasesMetadata.get (mapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ()))
      {
        throw new PlayMapLoadingException (
                Strings.format ("Country atlas [{}] for map [{}] is not loaded.", countryAtlasMetadata, mapMetadata));
      }

      countryAtlasesBuilder.add (assetManager.get (countryAtlasMetadata.getAssetDescriptor ()));
    }

    return countryAtlasesBuilder.build ();
  }

  @Override
  public void unload (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (!mapMetadataToCountryAtlasesMetadata.containsKey (mapMetadata))
    {
      log.warn ("Cannot unload country atlases for map [{}] because it is not loaded.", mapMetadata);
      return;
    }

    for (final CountryAtlasMetadata countryAtlasMetadata : mapMetadataToCountryAtlasesMetadata.get (mapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ()))
      {
        log.warn ("Cannot unload country atlas [{}] for map [{}] because it is not loaded.", countryAtlasMetadata,
                  mapMetadata);
        continue;
      }

      assetManager.unload (countryAtlasMetadata.getAssetDescriptor ());
    }

    mapMetadataToCountryAtlasesMetadata.removeAll (mapMetadata);
  }
}
