package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountryImageLoader
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (CountryImageLoader.class);
  private final Table <CountryName, CountryPrimaryImageState, CountryPrimaryImage> countryNamesAndPrimaryImageStatesToPrimaryImages = TreeBasedTable.create ();
  private final Table <CountryName, CountrySecondaryImageState, CountrySecondaryImage> countryNamesAndSecondaryImageStatesToSecondaryImages = TreeBasedTable.create ();
  private final Map <CountryName, CountryImages <CountryPrimaryImageState, CountryPrimaryImage>> countryNamesToPrimaryImages = new HashMap <> ();
  private final Map <CountryName, CountryImages <CountrySecondaryImageState, CountrySecondaryImage>> countryNamesToSecondaryImages = new HashMap <> ();
  private final Map <CountryName, Integer> countryNamesToAtlasIndices = new HashMap <> ();
  // @formatter:on

  public CountryImageLoader ()
  {
    int atlasIndex = 0;

    for (final TextureAtlas countryAtlas : Assets.countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        // TODO Adjacency line images are now in the country atlases - ignore them for now
        if (isAdjacencyLineImage (countryAtlasRegion)) continue;

        final String[] regionNameSegments = Strings.splitByUpperCase (countryAtlasRegion.name);
        final CountryName countryName = createCountryNameFrom (regionNameSegments);
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
          log.warn ("Unrecognized country texture atlas region [{}].", Strings.toString (regionNameSegments));
        }

        registerCountryAtlasIndex (countryName, atlasIndex);
      }

      ++atlasIndex;
    }

    // @formatter:off
    for (final CountryName countryName : countryNamesAndPrimaryImageStatesToPrimaryImages.rowKeySet ())
    {
      countryNamesToPrimaryImages.put (countryName,
                                       new CountryPrimaryImages (ImmutableMap.copyOf (
                                               countryNamesAndPrimaryImageStatesToPrimaryImages.row (countryName)),
                                               countryNamesToAtlasIndices.get (countryName)));
    }
    // @formatter:on

    // @formatter:off
    for (final CountryName countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesAndSecondaryImageStatesToSecondaryImages.put (
              countryName,
              CountrySecondaryImageState.NONE,
              new CountrySecondaryImage (null, countryName, CountrySecondaryImageState.NONE));
    }
    // @formatter:on

    // @formatter:off
    for (final CountryName countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesToSecondaryImages.put (countryName,
                                         new CountrySecondaryImages (
                                                 ImmutableMap.copyOf (
                                                         countryNamesAndSecondaryImageStatesToSecondaryImages.row (countryName)),
                                                 countryNamesToAtlasIndices.get (countryName)));
    }
    // @formatter:on
  }

  public CountryImages <CountryPrimaryImageState, CountryPrimaryImage> getAllPrimary (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesToPrimaryImages.containsKey (countryName), "Cannot find any "
            + CountryPrimaryImage.class.getSimpleName () + "'s for [" + countryName + "].");

    return countryNamesToPrimaryImages.get (countryName);
  }

  public CountryImages <CountrySecondaryImageState, CountrySecondaryImage>
          getAllSecondary (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesToSecondaryImages.containsKey (countryName), "Cannot find any "
            + CountrySecondaryImage.class.getSimpleName () + "'s for [" + countryName + "].");

    return countryNamesToSecondaryImages.get (countryName);
  }

  private static CountryName createCountryNameFrom (final String... countryAtlasRegionNameSegments)
  {
    final StringBuilder countryNameStringBuilder = new StringBuilder ();

    for (int i = 0; i < countryAtlasRegionNameSegments.length - 1; ++i)
    {
      countryNameStringBuilder.append (Strings.toProperCase (countryAtlasRegionNameSegments [i])).append (" ");
    }

    countryNameStringBuilder.deleteCharAt (countryNameStringBuilder.length () - 1);

    return new CountryName (countryNameStringBuilder.toString ());
  }

  private static boolean isAdjacencyLineImage (final TextureAtlas.AtlasRegion region)
  {
    return region.name.contains ("-");
  }

  // @formatter:off
  private static <E extends Enum <E> & CountryImageState <E>> E createCountryImageStateFrom (
          final Class <E> countryImageStateClass, final String... countryAtlasRegionNameSegments)
  {
    return E.valueOf (countryImageStateClass,
                      countryAtlasRegionNameSegments [countryAtlasRegionNameSegments.length - 1].toUpperCase ());
  }
  // @formatter:on

  // @formatter:off
  private static <E extends Enum <E> & CountryImageState <E>> boolean containsImageState (
          final Class <E> countryImageStateClass, final String... countryAtlasRegionNameSegments)
  {
    for (final E countryImageState : EnumSet.allOf (countryImageStateClass))
    {
      if (countryImageState.name ().equalsIgnoreCase (
              countryAtlasRegionNameSegments [countryAtlasRegionNameSegments.length - 1]))
      {
        return true;
      }
    }

    return false;
  }
  // @formatter:on

  private void registerCountryAtlasIndex (final CountryName countryName, final int atlasIndex)
  {
    final Integer oldAtlasIndex = countryNamesToAtlasIndices.put (countryName, atlasIndex);

    if (oldAtlasIndex != null && atlasIndex != oldAtlasIndex)
    {
      throw new IllegalStateException ("Atlas mismatch detected for country images with "
              + CountryName.class.getSimpleName () + " [" + countryName + "].\nExpected atlas index [" + oldAtlasIndex
              + "], but found atlas index [" + atlasIndex
              + "].\nAll images of a country must be in the same texture atlas.");
    }
  }
}
