package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.common.io.AbstractDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountryImageDataLoader extends AbstractDataLoader <String, CountryImageData>
{
  private final StreamParserFactory streamParserFactory;
  private ImmutableBiMap.Builder <String, CountryImageData> countryImageDataBuilder;
  private StreamParser streamParser;
  private String nameValue;
  private int referenceWidth;
  private int referenceHeight;
  private int referenceDestinationX;
  private int referenceDestinationY;
  private int referenceTextUpperLeftX;
  private int referenceTextUpperLeftY;

  public CountryImageDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected ImmutableBiMap <String, CountryImageData> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countryImageDataBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    countryImageDataBuilder = new ImmutableBiMap.Builder <> ();
    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    nameValue = streamParser.getNextQuotedString ();
    referenceWidth = streamParser.getNextInteger ();
    referenceHeight = streamParser.getNextInteger ();
    referenceDestinationX = streamParser.getNextInteger ();
    referenceDestinationY = streamParser.getNextInteger ();
    referenceTextUpperLeftX = streamParser.getNextInteger ();
    referenceTextUpperLeftY = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    final Vector2 referenceSize = new Vector2 (referenceWidth, referenceHeight);
    final Vector2 referenceDestination = new Vector2 (referenceDestinationX, referenceDestinationY);
    final Vector2 referenceTextUpperLeft = new Vector2 (referenceTextUpperLeftX, referenceTextUpperLeftY);

    countryImageDataBuilder
            .put (nameValue,
                  new CountryImageData (nameValue, referenceDestination, referenceTextUpperLeft, referenceSize));
  }
}
