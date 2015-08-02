package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.shared.io.AbstractDataLoader;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CountryModelDataLoader extends AbstractDataLoader <Id, Country>
{
  private static final Logger log = LoggerFactory.getLogger (CountryModelDataLoader.class);
  private final ImmutableBiMap.Builder <Id, Country> countriesBuilder = new ImmutableBiMap.Builder <> ();
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
  protected ImmutableBiMap <Id, Country> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return countriesBuilder.build ();
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
    final Country country = CountryFactory.builder (name).build ();

    log.debug ("Successfully loaded [{}] from file [{}].", country, fileName);

    countriesBuilder.put (country.getId (), country);
  }
}
