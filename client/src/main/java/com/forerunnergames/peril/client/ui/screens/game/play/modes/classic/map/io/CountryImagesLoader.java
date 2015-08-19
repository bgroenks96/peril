package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImage;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.NullCountryPrimaryImages;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.NullCountrySecondaryImages;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
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

public final class CountryImagesLoader
{
  // @formatter:off
  private static final Logger log = LoggerFactory.getLogger (CountryImagesLoader.class);
  private final Table <CountryName, CountryPrimaryImageState, CountryPrimaryImage> countryNamesAndPrimaryImageStatesToPrimaryImages = TreeBasedTable.create ();
  private final Table <CountryName, CountrySecondaryImageState, CountrySecondaryImage> countryNamesAndSecondaryImageStatesToSecondaryImages = TreeBasedTable.create ();
  private final Map <CountryName, CountryImages <CountryPrimaryImageState, CountryPrimaryImage>> countryNamesToPrimaryImages = new HashMap <> ();
  private final Map <CountryName, CountryImages <CountrySecondaryImageState, CountrySecondaryImage>> countryNamesToSecondaryImages = new HashMap <> ();
  private final Map <CountryName, Integer> countryNamesToAtlasIndices = new HashMap <> ();
  // @formatter:on

  public CountryImagesLoader (final MapMetadata mapMetadata, final ImmutableList <TextureAtlas> countryAtlases)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (countryAtlases, "countryAtlases");
    Arguments.checkHasNoNullElements (countryAtlases, "countryAtlases");

    log.info ("Loading country images for map [{}]...", mapMetadata);

    int atlasIndex = 0;

    for (final TextureAtlas countryAtlas : countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        final ImmutableList <String> regionNameSegments = createRegionNameSegments (countryAtlasRegion, mapMetadata);
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
          invalidCountryImageState (regionNameSegments, countryAtlasRegion, mapMetadata);
        }

        registerCountryAtlasIndex (countryName, atlasIndex);
      }

      ++atlasIndex;
    }

    for (final CountryName countryName : countryNamesAndPrimaryImageStatesToPrimaryImages.rowKeySet ())
    {
      countryNamesToPrimaryImages.put (countryName,
                                       new CountryPrimaryImages (
                                               ImmutableMap.copyOf (countryNamesAndPrimaryImageStatesToPrimaryImages
                                                       .row (countryName)),
                                               countryNamesToAtlasIndices.get (countryName)));
    }

    for (final CountryName countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesAndSecondaryImageStatesToSecondaryImages
              .put (countryName, CountrySecondaryImageState.NONE,
                    new CountrySecondaryImage (null, countryName, CountrySecondaryImageState.NONE));
    }

    for (final CountryName countryName : countryNamesAndSecondaryImageStatesToSecondaryImages.rowKeySet ())
    {
      countryNamesToSecondaryImages
              .put (countryName,
                    new CountrySecondaryImages (
                            ImmutableMap
                                    .copyOf (countryNamesAndSecondaryImageStatesToSecondaryImages.row (countryName)),
                            countryNamesToAtlasIndices.get (countryName)));
    }

    log.info ("Finished loading country images for map [{}].", mapMetadata);
  }

  public CountryImages <CountryPrimaryImageState, CountryPrimaryImage> getAllPrimary (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToPrimaryImages.containsKey (countryName))
    {
      log.warn ("Cannot find any {}'s for [{}].", CountryPrimaryImage.class.getSimpleName (), countryName);
      return new NullCountryPrimaryImages (countryName);
    }

    return countryNamesToPrimaryImages.get (countryName);
  }

  public CountryImages <CountrySecondaryImageState, CountrySecondaryImage> getAllSecondary (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToSecondaryImages.containsKey (countryName))
    {
      log.warn ("Cannot find any {}'s for [{}].", CountrySecondaryImage.class.getSimpleName (), countryName);
      return new NullCountrySecondaryImages (countryName);
    }

    return countryNamesToSecondaryImages.get (countryName);
  }

  private static CountryName createCountryNameFrom (final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    assert countryAtlasRegionNameSegments != null;
    assert countryAtlasRegionNameSegments.size () >= 2;

    final StringBuilder countryNameStringBuilder = new StringBuilder ();

    for (int i = 0; i < countryAtlasRegionNameSegments.size () - 1; ++i)
    {
      countryNameStringBuilder.append (Strings.toProperCase (countryAtlasRegionNameSegments.get (i))).append (" ");
    }

    countryNameStringBuilder.deleteCharAt (countryNameStringBuilder.lastIndexOf (" "));

    log.trace ("Created country name [{}] from country atlas region name segments [{}]",
               countryNameStringBuilder.toString (), countryAtlasRegionNameSegments);

    return new CountryName (countryNameStringBuilder.toString ());
  }

  private static <E extends Enum <E> & CountryImageState <E>> E createCountryImageStateFrom (final Class <E> countryImageStateClass,
                                                                                             final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    final E countryImageState = E
            .valueOf (countryImageStateClass,
                      countryAtlasRegionNameSegments.get (countryAtlasRegionNameSegments.size () - 1).toUpperCase ());

    log.trace ("Created country image state [{}] from country atlas region name segments [{}]", countryImageState,
               countryAtlasRegionNameSegments);

    return countryImageState;
  }

  private static <E extends Enum <E> & CountryImageState <E>> boolean containsImageState (final Class <E> countryImageStateClass,
                                                                                          final ImmutableList <String> countryAtlasRegionNameSegments)
  {
    for (final E countryImageState : EnumSet.allOf (countryImageStateClass))
    {
      if (countryImageState.name ()
              .equalsIgnoreCase (countryAtlasRegionNameSegments.get (countryAtlasRegionNameSegments.size () - 1)))
      {
        return true;
      }
    }

    return false;
  }

  private void invalidCountryImageState (final ImmutableList <String> countryAtlasRegionNameSegments,
                                         final TextureAtlas.AtlasRegion countryAtlasRegion,
                                         final MapMetadata mapMetadata)
  {
    final String invalidCountryImageStateAtlasRegionNameSegment = countryAtlasRegionNameSegments
            .get (countryAtlasRegionNameSegments.size () - 1);

    throw new PlayMapLoadingException (
            Strings.format ("Invalid country image state [{}] in country texture atlas region [{}] for map [{}].\nValid country image states: {}'s [{}], {}'s [{}].",
                            invalidCountryImageStateAtlasRegionNameSegment, countryAtlasRegion, mapMetadata,
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
      countryImageStatesBuilder.add (countryImageState.toProperCase ());
    }

    return countryImageStatesBuilder.build ();
  }

  private ImmutableList <String> createRegionNameSegments (final TextureAtlas.AtlasRegion countryAtlasRegion,
                                                           final MapMetadata mapMetadata)
  {
    if (countryAtlasRegion.name == null)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Empty country atlas region name detected for map [{}].", mapMetadata));
    }

    final ImmutableList <String> regionNameSegments = ImmutableList
            .copyOf (Strings.splitByUpperCase (countryAtlasRegion.name));

    if (regionNameSegments.size () < 2)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Invalid country atlas region name [{}] detected for map [{}].", countryAtlasRegion,
                              mapMetadata));
    }

    return regionNameSegments;
  }

  private void registerCountryAtlasIndex (final CountryName countryName, final int atlasIndex)
  {
    final Integer oldAtlasIndex = countryNamesToAtlasIndices.put (countryName, atlasIndex);

    if (oldAtlasIndex != null && atlasIndex != oldAtlasIndex)
    {
      throw new PlayMapLoadingException (
              Strings.format ("Atlas mismatch detected for country images with {} [{}].\nExpected atlas index [{}], but found atlas index [{}].\nAll images of a country must be in the same texture atlas.",
                              CountryName.class.getSimpleName (), countryName, oldAtlasIndex, atlasIndex));
    }
  }
}
