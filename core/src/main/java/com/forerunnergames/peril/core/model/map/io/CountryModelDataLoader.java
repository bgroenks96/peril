package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.AbstractDataLoader;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountryModelDataLoader extends AbstractDataLoader <CountryFactory>
{
  private static final Logger log = LoggerFactory.getLogger (CountryModelDataLoader.class);
  private final CountryFactory factory = new CountryFactory ();
  private final StreamParserFactory streamParserFactory;
  private StreamParser streamParser;
  private String fileName;
  private String name;

  public CountryModelDataLoader (final StreamParserFactory streamParserFactory)
  {
    Arguments.checkIsNotNull (streamParserFactory, "streamParserFactory");

    this.streamParserFactory = streamParserFactory;
  }

  @Override
  protected CountryFactory finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return factory;
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    this.fileName = fileName;
    streamParser = streamParserFactory.create (fileName);
  }

  @Override
  protected boolean readData ()
  {
    name = streamParser.getNextQuotedString ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    factory.newCountryWith (name);

    log.debug ("Successfully loaded country [{}] from file [{}].", name, fileName);
  }
}
