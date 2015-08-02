package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.TerritoryColor;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.territory.TerritoryName;
import com.forerunnergames.peril.core.shared.io.AbstractDataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

public abstract class AbstractTerritoryColorToNameLoader <T extends TerritoryColor <?>, U extends TerritoryName>
        extends AbstractDataLoader <T, U>implements TerritoryColorToNameLoader <T, U>
{
  private final ImmutableBiMap.Builder <T, U> territoryColorsToNames = new ImmutableBiMap.Builder <> ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private int territoryColorComponentValue;
  private String territoryNameValue;

  protected AbstractTerritoryColorToNameLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected final ImmutableBiMap <T, U> finalizeData ()
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
    territoryNameValue = streamParser.getNextQuotedString ();
    territoryColorComponentValue = streamParser.getNextInteger ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected final void saveData ()
  {
    territoryColorsToNames.put (createTerritoryColor (territoryColorComponentValue),
                                createTerritoryName (territoryNameValue));
  }

  protected abstract T createTerritoryColor (final int colorComponentValue);

  protected abstract U createTerritoryName (final String nameValue);
}
