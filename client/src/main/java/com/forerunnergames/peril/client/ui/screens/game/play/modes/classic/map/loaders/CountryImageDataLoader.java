package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders;

import com.forerunnergames.peril.client.io.AbstractDataLoader;
import com.forerunnergames.peril.client.io.StreamParserFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountryImageDataLoader extends AbstractDataLoader <CountryName, CountryImageData>
{
  private final ImmutableBiMap.Builder <CountryName, CountryImageData> countryImageDataBuilder = new ImmutableBiMap.Builder <> ();
  private StreamParser streamParser;
  private String nameValue;
  private int width;
  private int height;
  private int destPlayMapX;
  private int destPlayMapY;
  private int textUpperLeftX;
  private int textUpperLeftY;

  @Override
  protected ImmutableBiMap <CountryName, CountryImageData> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countryImageDataBuilder.build ();
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
    nameValue = streamParser.getNextQuotedString ();
    width = streamParser.getNextInteger ();
    height = streamParser.getNextInteger ();
    destPlayMapX = streamParser.getNextInteger ();
    destPlayMapY = streamParser.getNextInteger ();
    textUpperLeftX = streamParser.getNextInteger ();
    textUpperLeftY = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final CountryName name = new CountryName (nameValue);
    final Size2D size = new Size2D (width, height);
    final Point2D destPlayMap = new Point2D (destPlayMapX, destPlayMapY);
    final Point2D textUpperLeft = new Point2D (textUpperLeftX, textUpperLeftY);

    countryImageDataBuilder.put (name, new CountryImageData (name, destPlayMap, textUpperLeft, size));
  }
}
