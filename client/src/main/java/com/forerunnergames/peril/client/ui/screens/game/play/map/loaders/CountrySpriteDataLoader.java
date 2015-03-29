package com.forerunnergames.peril.client.ui.screens.game.play.map.loaders;

import com.forerunnergames.peril.client.io.AbstractDataLoader;
import com.forerunnergames.peril.client.io.StreamParserFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteData;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountrySpriteDataLoader extends AbstractDataLoader <CountryName, CountrySpriteData>
{
  private static final int SPRITES_PER_ROW = 13;
  private final ImmutableBiMap.Builder <CountryName, CountrySpriteData> countrySpriteDataBuilder = new ImmutableBiMap.Builder <> ();
  private StreamParser streamParser;
  private String countryNameValue;
  private int spriteWidth;
  private int spriteHeight;
  private int spriteDestPlayMapX;
  private int spriteDestPlayMapY;
  private int spriteCenterX;
  private int spriteCenterY;

  @Override
  protected ImmutableBiMap <CountryName, CountrySpriteData> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countrySpriteDataBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = StreamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    countryNameValue = streamParser.getNextQuotedString ();
    spriteWidth = streamParser.getNextInteger ();
    spriteHeight = streamParser.getNextInteger ();
    spriteDestPlayMapX = streamParser.getNextInteger ();
    spriteDestPlayMapY = streamParser.getNextInteger ();
    spriteCenterX = streamParser.getNextInteger ();
    spriteCenterY = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final CountryName countryName = new CountryName (countryNameValue);
    final Size2D spriteSize = new Size2D (spriteWidth, spriteHeight);
    final Point2D spriteDestPlayMap = new Point2D (spriteDestPlayMapX, spriteDestPlayMapY);
    final Point2D spriteCenter = new Point2D (spriteCenterX, spriteCenterY);

    countrySpriteDataBuilder.put (countryName, new CountrySpriteData (countryName, spriteDestPlayMap, spriteCenter,
            spriteSize, SPRITES_PER_ROW));
  }
}
