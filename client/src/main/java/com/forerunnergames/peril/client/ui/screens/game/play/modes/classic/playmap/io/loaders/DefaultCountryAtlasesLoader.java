/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryAtlasMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
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
  private final Multimap <PlayMapMetadata, CountryAtlasMetadata> playMapMetadataToCountryAtlasesMetadata = HashMultimap.create ();
  private final CountryAtlasMetadataLoader countryAtlasMetadataLoader = new DefaultCountryAtlasMetadataLoader ();
  private final AssetManager assetManager;
  // @formatter:on

  public DefaultCountryAtlasesLoader (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void load (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (!playMapMetadataToCountryAtlasesMetadata.containsKey (playMapMetadata),
                          Strings.format ("Country atlases were already loaded for play map [{}].", playMapMetadata));

    for (final CountryAtlasMetadata countryAtlasMetadata : countryAtlasMetadataLoader.load (playMapMetadata))
    {
      assetManager.load (countryAtlasMetadata.getAssetDescriptor ());
      playMapMetadataToCountryAtlasesMetadata.put (playMapMetadata, countryAtlasMetadata);
    }
  }

  @Override
  public boolean isFinishedLoading (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (playMapMetadataToCountryAtlasesMetadata.containsKey (playMapMetadata),
                          Strings.format ("Country atlases were never loaded for play map [{}].", playMapMetadata));

    for (final CountryAtlasMetadata countryAtlasMetadata : playMapMetadataToCountryAtlasesMetadata
            .get (playMapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ())) return false;
    }

    return true;
  }

  @Override
  public ImmutableList <TextureAtlas> get (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions
            .checkIsTrue (playMapMetadataToCountryAtlasesMetadata.containsKey (playMapMetadata),
                          Strings.format ("Country atlases were never loaded for play map [{}].", playMapMetadata));

    final ImmutableList.Builder <TextureAtlas> countryAtlasesBuilder = ImmutableList.builder ();

    for (final CountryAtlasMetadata countryAtlasMetadata : playMapMetadataToCountryAtlasesMetadata
            .get (playMapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ()))
      {
        throw new PlayMapLoadingException (Strings.format ("Country atlas [{}] for map [{}] is not loaded.",
                                                           countryAtlasMetadata, playMapMetadata));
      }

      countryAtlasesBuilder.add (assetManager.get (countryAtlasMetadata.getAssetDescriptor ()));
    }

    return countryAtlasesBuilder.build ();
  }

  @Override
  public void unload (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (!playMapMetadataToCountryAtlasesMetadata.containsKey (playMapMetadata))
    {
      log.warn ("Cannot unload country atlases for play map [{}] because it is not loaded.", playMapMetadata);
      return;
    }

    for (final CountryAtlasMetadata countryAtlasMetadata : playMapMetadataToCountryAtlasesMetadata
            .get (playMapMetadata))
    {
      if (!assetManager.isLoaded (countryAtlasMetadata.getAssetDescriptor ()))
      {
        log.warn ("Cannot unload country atlas [{}] for play map [{}] because it is not loaded.", countryAtlasMetadata,
                  playMapMetadata);
        continue;
      }

      assetManager.unload (countryAtlasMetadata.getAssetDescriptor ());
    }

    playMapMetadataToCountryAtlasesMetadata.removeAll (playMapMetadata);
  }
}
