package com.forerunnergames.peril.client.ui.screens.game.play.map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public final class CountrySprites
{
  private final Table <CountryName, CountrySpriteState, Sprite> countryNamesAndSpriteStatesToSprites = HashBasedTable.create ();
  private StringBuilder countryNameStringBuilder = new StringBuilder ();

  public CountrySprites ()
  {
    for (final TextureAtlas countryAtlas : Assets.countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        final String[] regionNameSegments = Strings.splitByUpperCase (countryAtlasRegion.name);
        final CountryName countryName = createCountryNameFrom (regionNameSegments);
        final CountrySpriteState countryCountrySpriteState = createCountryStateFrom (regionNameSegments);
        final Sprite countrySprite = countryAtlas.createSprite (countryAtlasRegion.name);

        countryNamesAndSpriteStatesToSprites.put (countryName, countryCountrySpriteState, countrySprite);
      }
    }
  }

  public Sprite get (final CountryName countryName, final CountrySpriteState countrySpriteState)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countrySpriteState, "countrySpriteState");

    final Sprite countrySprite = countryNamesAndSpriteStatesToSprites.get (countryName, countrySpriteState);

    if (countrySprite == null)
    {
      throw new IllegalStateException ("Cannot find country sprite named [" + countryName + "] with state ["
              + countrySpriteState + "].");
    }

    return countrySprite;
  }

  private CountryName createCountryNameFrom (final String[] countryAtlasRegionNameSegments)
  {
    countryNameStringBuilder = Strings.clear (countryNameStringBuilder);

    for (int i = 0; i < countryAtlasRegionNameSegments.length - 1; ++i)
    {
      countryNameStringBuilder.append (Strings.toProperCase (countryAtlasRegionNameSegments[i])).append (" ");
    }

    countryNameStringBuilder.deleteCharAt (countryNameStringBuilder.length () - 1);

    return new CountryName (countryNameStringBuilder.toString ());
  }

  private CountrySpriteState createCountryStateFrom (final String[] countryAtlasRegionNameSegments)
  {
    return CountrySpriteState.valueOf (countryAtlasRegionNameSegments [countryAtlasRegionNameSegments.length - 1]
            .toUpperCase ());
  }
}
