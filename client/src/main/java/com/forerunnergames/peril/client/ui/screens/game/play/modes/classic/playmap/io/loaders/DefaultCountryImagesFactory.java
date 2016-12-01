/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImages;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryImagesFactory implements CountryImagesFactory
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryImagesFactory.class);
  private final Map <String, Integer> countryNamesToAtlasIndices = new HashMap <> ();
  private final Map <String, CountryPrimaryImages> countryNamesToPrimaryImages = new HashMap <> ();
  private final Map <String, CountrySecondaryImages> countryNamesToSecondaryImages = new HashMap <> ();
  private final Table <String, CountryPrimaryImageState, CountryPrimaryImage> countryNamesAndPrimaryImageStatesToPrimaryImages = TreeBasedTable.create ();
  private final Table <String, CountrySecondaryImageState, CountrySecondaryImage> countryNamesAndSecondaryImageStatesToSecondaryImages = TreeBasedTable.create ();
  private PlayMapMetadata loadedPlayMapMetadata = PlayMapMetadata.NULL;
  // @formatter:on

  @Override
  public void create (final PlayMapMetadata playMapMetadata, final ImmutableList <TextureAtlas> countryAtlases)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Arguments.checkIsNotNull (countryAtlases, "countryAtlases");
    Arguments.checkHasNoNullElements (countryAtlases, "countryAtlases");

    log.debug ("Creating country images for play map [{}]...", playMapMetadata);

    destroy ();

    int atlasIndex = 0;

    for (final TextureAtlas countryAtlas : countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        final ImmutableList <String> regionNameSegments = createRegionNameSegments (countryAtlasRegion,
                                                                                    playMapMetadata);
        final String countryName = createCountryNameFrom (regionNameSegments);
        final SpriteDrawable countryDrawable = new SpriteDrawable (countryAtlas.createSprite (countryAtlasRegion.name));

        if (containsImageState (CountryPrimaryImageState.class, regionNameSegments))
        {
          final CountryPrimaryImageState countryPrimaryImageState = createCountryImageStateFrom (CountryPrimaryImageState.class,
                                                                                                 regionNameSegments);
          final CountryPrimaryImage countryPrimaryImage = new CountryPrimaryImage (countryDrawable, countryName,
                  countryPrimaryImageState);
          countryNamesAndPrimaryImageStatesToPrimaryImages.put (countryName, countryPrimaryImageState,
                                                                countryPrimaryImage);
        }
        else if (containsImageState (CountrySecondaryImageState.class, regionNameSegments))
        {
          final CountrySecondaryImageState countrySecondaryImageState = createCountryImageStateFrom (CountrySecondaryImageState.class,
                                                                                                     regionNameSegments);
          final CountrySecondaryImage countrySecondaryImage = new CountrySecondaryImage (countryDrawable, countryName,
                  countrySecondaryImageState);
          countryNamesAndSecondaryImageStatesToSecondaryImages.put (countryName, countrySecondaryImageState,
                                                                    countrySecondaryImage);
        }
        else
        {
          invalidCountryImageState (regionNameSegments, countryAtlasRegion, playMapMetadata);
        }

        registerCountryAtlasIndex (countryName, atlasIndex);
      }

      ++atlasIndex;
    }

    for (final String countryName : countryNamesAndPrimaryImageStatesToPrimaryImages.rowKeySet ())
    {
      countryNamesToPrimaryImages
              .put (countryName,
                    new CountryPrimaryImages (
                            ImmutableMap.copyOf (countryNamesAndPrimaryImageStatesToPrimaryImages.row (countryName)),
                            countryNamesToAtlasIndices.get (countryName)));
    }

    for (final String countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesAndSecondaryImageStatesToSecondaryImages
              .put (countryName, CountrySecondaryImageState.NONE,
                    new CountrySecondaryImage (null, countryName, CountrySecondaryImageState.NONE));
    }

    for (final String countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesToSecondaryImages.put (countryName, new CountrySecondaryImages (
              ImmutableMap.copyOf (countryNamesAndSecondaryImageStatesToSecondaryImages.row (countryName)),
              countryNamesToAtlasIndices.get (countryName)));
    }

    loadedPlayMapMetadata = playMapMetadata;

    log.debug ("Finished creating country images for play map [{}].", playMapMetadata);
  }

  @Override
  public ImmutableMap <String, CountryPrimaryImages> getPrimary (final PlayMapMetadata playMapMetadata)

  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions.checkIsTrue (isLoaded (playMapMetadata),
                               Strings.format ("{} for {}: [{}] are not loaded.",
                                               CountryPrimaryImages.class.getSimpleName (),
                                               PlayMapMetadata.class.getSimpleName (), playMapMetadata));

    return ImmutableMap.copyOf (countryNamesToPrimaryImages);
  }

  @Override
  public ImmutableMap <String, CountrySecondaryImages> getSecondary (final PlayMapMetadata playMapMetadata)

  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");
    Preconditions.checkIsTrue (isLoaded (playMapMetadata),
                               Strings.format ("{} for {}: [{}] are not loaded.",
                                               CountrySecondaryImages.class.getSimpleName (),
                                               PlayMapMetadata.class.getSimpleName (), playMapMetadata));

    return ImmutableMap.copyOf (countryNamesToSecondaryImages);
  }

  @Override
  public void destroy ()
  {
    countryNamesToAtlasIndices.clear ();
    countryNamesToPrimaryImages.clear ();
    countryNamesToSecondaryImages.clear ();
    countryNamesAndPrimaryImageStatesToPrimaryImages.clear ();
    countryNamesAndSecondaryImageStatesToSecondaryImages.clear ();
    loadedPlayMapMetadata = PlayMapMetadata.NULL;
  }

  private static String createCountryNameFrom (final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    final String countryName = Strings.toProperCase (countryAtlasRegionNameSegments.get (0));

    log.trace ("Created country name [{}] from country atlas region name segments [{}]", countryName,
               countryAtlasRegionNameSegments);

    return countryName;
  }

  private static <E extends Enum <E> & CountryImageState <E>> E createCountryImageStateFrom (final Class <E> countryImageStateClass,
                                                                                             final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    final E countryImageState = Enum.valueOf (countryImageStateClass, countryAtlasRegionNameSegments
            .get (countryAtlasRegionNameSegments.size () - 1).toUpperCase ().replace (" ", "_"));

    log.trace ("Created country image state [{}] from country atlas region name segments [{}]", countryImageState,
               countryAtlasRegionNameSegments);

    return countryImageState;
  }

  private static <E extends Enum <E> & CountryImageState <E>> boolean containsImageState (final Class <E> countryImageStateClass,
                                                                                          final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    for (final E countryImageState : EnumSet.allOf (countryImageStateClass))
    {
      if (countryImageState.name ().equalsIgnoreCase (countryAtlasRegionNameSegments
              .get (countryAtlasRegionNameSegments.size () - 1).replace (" ", "_")))
      {
        return true;
      }
    }

    return false;
  }

  private static ImmutableList <String> createRegionNameSegments (final TextureAtlas.AtlasRegion countryAtlasRegion,
                                                                  final PlayMapMetadata playMapMetadata)
  {
    if (countryAtlasRegion.name == null)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Empty country atlas region name detected for map [{}].", playMapMetadata));
    }

    final ImmutableList <String> regionNameSegments = ImmutableList.copyOf (countryAtlasRegion.name.split (" - "));

    if (regionNameSegments.size () != 2)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Invalid country atlas region name [{}] detected for map [{}].", countryAtlasRegion,
                              playMapMetadata));
    }

    return regionNameSegments;
  }

  private boolean isLoaded (final PlayMapMetadata playMapMetadata)
  {
    return loadedPlayMapMetadata.equals (playMapMetadata);
  }

  private void invalidCountryImageState (final ImmutableList <String> countryAtlasRegionNameSegments,
                                         final TextureAtlas.AtlasRegion countryAtlasRegion,
                                         final PlayMapMetadata playMapMetadata)
  {
    final String invalidCountryImageStateAtlasRegionNameSegment = countryAtlasRegionNameSegments
            .get (countryAtlasRegionNameSegments.size () - 1);

    throw new PlayMapLoadingException (
            Strings.format ("Invalid country image state [{}] in country texture atlas region [{}] for map [{}].\n"
                    + "Valid country image states: {}'s [{}], {}'s [{}].",
                            invalidCountryImageStateAtlasRegionNameSegment, countryAtlasRegion, playMapMetadata,
                            CountryPrimaryImageState.class.getSimpleName (),
                            getValidCountryImageStateAtlasRegionNameSegments (CountryPrimaryImageState.class),
                            CountrySecondaryImageState.class.getSimpleName (),
                            getValidCountryImageStateAtlasRegionNameSegments (CountrySecondaryImageState.class)));
  }

  private <E extends Enum <E> & CountryImageState <E>> ImmutableSet <String> getValidCountryImageStateAtlasRegionNameSegments (final Class <E> countryImageStateClass)

  {
    final ImmutableSet.Builder <String> countryImageStatesBuilder = ImmutableSet.builder ();

    for (final E countryImageState : EnumSet.allOf (countryImageStateClass))
    {
      countryImageStatesBuilder.add (Strings.toProperCase (countryImageState.getEnumName ().replace ("_", " ")));
    }

    return countryImageStatesBuilder.build ();
  }

  private void registerCountryAtlasIndex (final String countryName, final int atlasIndex)
  {
    final Integer oldAtlasIndex = countryNamesToAtlasIndices.put (countryName, atlasIndex);

    if (oldAtlasIndex != null && atlasIndex != oldAtlasIndex)
    {
      throw new PlayMapLoadingException (Strings.format (
                                                         "Atlas mismatch detected for country images with name [{}].\n"
                                                                 + "Expected atlas index [{}], but found atlas index [{}].\n"
                                                                 + "All images of a country must be in the same texture atlas.",
                                                         countryName, oldAtlasIndex, atlasIndex));
    }
  }
}
