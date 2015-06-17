package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.core.shared.io.AbstractDataLoader;
import com.forerunnergames.peril.client.io.LibGdxStreamParserFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public final class CountryImageDataLoader extends AbstractDataLoader <CountryName, CountryImageData>
{
  private final ImmutableBiMap.Builder <CountryName, CountryImageData> countryImageDataBuilder = new ImmutableBiMap.Builder <> ();
  private StreamParser streamParser;
  private String nameValue;
  private int referenceWidth;
  private int referenceHeight;
  private int referenceDestinationX;
  private int referenceDestinationY;
  private int referenceTextUpperLeftX;
  private int referenceTextUpperLeftY;

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

    streamParser = LibGdxStreamParserFactory.create (fileName);
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
    final CountryName name = new CountryName (nameValue);
    final Vector2 referenceSize = new Vector2 (referenceWidth, referenceHeight);
    final Vector2 referenceDestination = new Vector2 (referenceDestinationX, referenceDestinationY);
    final Vector2 referenceTextUpperLeft = new Vector2 (referenceTextUpperLeftX, referenceTextUpperLeftY);

    countryImageDataBuilder.put (name, new CountryImageData (name, referenceDestination, referenceTextUpperLeft,
            referenceSize));
  }
}
