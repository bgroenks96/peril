package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.common.io.AbstractBiMapDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.id.IdGenerator;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountryNamesDataLoader extends AbstractBiMapDataLoader <Id, String>
{
  private static final Logger log = LoggerFactory.getLogger (CountryNamesDataLoader.class);
  private ImmutableBiMap.Builder <Id, String> countryNamesBuilder;
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private String fileName;
  private String countryName;

  public CountryNamesDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected ImmutableBiMap <Id, String> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countryNamesBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    this.fileName = fileName;
    streamParser = streamParserFactory.create (fileName);
    countryNamesBuilder = new ImmutableBiMap.Builder <> ();
  }

  @Override
  protected boolean readData ()
  {
    countryName = streamParser.getNextQuotedString ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    log.debug ("Successfully read [{}] from file [{}].", countryName, fileName);

    countryNamesBuilder.put (IdGenerator.generateUniqueId (), countryName);
  }
}
