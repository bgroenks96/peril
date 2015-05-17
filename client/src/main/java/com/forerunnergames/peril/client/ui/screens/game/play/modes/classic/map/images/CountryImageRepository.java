package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.TreeBasedTable;

import java.util.SortedMap;

public final class CountryImageRepository
{
  private final TreeBasedTable <CountryName, CountryImageState, Image> countryNamesAndImageStatesToImages = TreeBasedTable
          .create ();

  public CountryImageRepository ()
  {
    for (final TextureAtlas countryAtlas : Assets.countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        final String[] regionNameSegments = Strings.splitByUpperCase (countryAtlasRegion.name);
        final CountryName countryName = createCountryNameFrom (regionNameSegments);
        final CountryImageState countryImageState = createCountryImageStateFrom (regionNameSegments);
        final Image countryImage = new Image (new SpriteDrawable (countryAtlas.createSprite (countryAtlasRegion.name)));

        countryNamesAndImageStatesToImages.put (countryName, countryImageState, countryImage);
      }
    }
  }

  public Image get (final CountryName countryName, final CountryImageState countryImageState)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countryImageState, "countryImageState");
    Preconditions.checkIsTrue (countryNamesAndImageStatesToImages.containsValue (countryName), "Cannot find "
            + Image.class.getSimpleName () + " with " + CountryName.class.getSimpleName () + " [" + countryName
            + "] & " + CountryImageState.class.getSimpleName () + " [" + countryImageState + "].");

    return countryNamesAndImageStatesToImages.get (countryName, countryImageState);
  }

  public SortedMap <CountryImageState, Image> getAll (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesAndImageStatesToImages.containsRow (countryName), "Cannot find "
            + Image.class.getSimpleName () + "'s with " + CountryName.class.getSimpleName () + " [" + countryName
            + "].");

    return countryNamesAndImageStatesToImages.row (countryName);
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

  private static CountryImageState createCountryImageStateFrom (final String... countryAtlasRegionNameSegments)
  {
    return CountryImageState.valueOf (countryAtlasRegionNameSegments [countryAtlasRegionNameSegments.length - 1]
            .toUpperCase ());
  }
}
