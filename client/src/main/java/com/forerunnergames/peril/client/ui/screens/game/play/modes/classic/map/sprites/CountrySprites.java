package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.Map;

public final class CountrySprites
{
  private final Map <CountryName, CountrySprite> countryNamesToSprites = new HashMap <> ();
  private StringBuilder countryNameStringBuilder = new StringBuilder ();

  public CountrySprites ()
  {
    final Table <CountryName, CountrySpriteState, Sprite> countryNamesAndSpriteStatesToSprites = HashBasedTable
            .create ();

    for (final TextureAtlas countryAtlas : Assets.countryAtlases)
    {
      for (final TextureAtlas.AtlasRegion countryAtlasRegion : countryAtlas.getRegions ())
      {
        final String[] regionNameSegments = Strings.splitByUpperCase (countryAtlasRegion.name);
        final CountryName countryName = createCountryNameFrom (regionNameSegments);
        final CountrySpriteState countryCountrySpriteState = createCountryStateFrom (regionNameSegments);
        final Sprite sprite = countryAtlas.createSprite (countryAtlasRegion.name);

        countryNamesAndSpriteStatesToSprites.put (countryName, countryCountrySpriteState, sprite);
      }
    }

    for (final CountryName countryName : countryNamesAndSpriteStatesToSprites.rowKeySet ())
    {
      countryNamesToSprites.put (countryName,
                                 new CountrySprite (countryNamesAndSpriteStatesToSprites.row (countryName)));
    }
  }

  public CountrySprite get (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Preconditions.checkIsTrue (countryNamesToSprites.containsKey (countryName), "Cannot find country sprite named ["
            + countryName + "].");

    return countryNamesToSprites.get (countryName);
  }

  private CountryName createCountryNameFrom (final String[] countryAtlasRegionNameSegments)
  {
    countryNameStringBuilder = Strings.clear (countryNameStringBuilder);

    for (int i = 0; i < countryAtlasRegionNameSegments.length - 1; ++i)
    {
      countryNameStringBuilder.append (Strings.toProperCase (countryAtlasRegionNameSegments [i])).append (" ");
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
