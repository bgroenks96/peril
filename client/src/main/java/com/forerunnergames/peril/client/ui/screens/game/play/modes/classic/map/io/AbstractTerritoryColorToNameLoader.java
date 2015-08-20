package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.shared.io.AbstractDataLoader;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public abstract class AbstractTerritoryColorToNameLoader <T extends TerritoryColor <?>>
        extends AbstractDataLoader <T, String> implements TerritoryColorToNameLoader <T>
{
  private final ImmutableBiMap.Builder <T, String> territoryColorsToNames = new ImmutableBiMap.Builder <> ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private int territoryColorComponentValue;
  private String territoryName;

  protected AbstractTerritoryColorToNameLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected final ImmutableBiMap <T, String> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return territoryColorsToNames.build ();
  }

  @Override
  protected final void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected final boolean readData ()
  {
    territoryName = streamParser.getNextQuotedString ();
    territoryColorComponentValue = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected final void saveData ()
  {
    territoryColorsToNames.put (createTerritoryColor (territoryColorComponentValue), territoryName);
  }

  protected abstract T createTerritoryColor (final int colorComponentValue);
}
