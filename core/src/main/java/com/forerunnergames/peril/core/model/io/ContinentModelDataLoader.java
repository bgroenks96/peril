package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.shared.io.AbstractDataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.common.io.StreamParser;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ContinentModelDataLoader extends AbstractDataLoader <Id, Continent>
{
  private static final Logger log = LoggerFactory.getLogger (ContinentModelDataLoader.class);
  private final ImmutableBiMap.Builder <Id, Continent> continentsBuilder = new ImmutableBiMap.Builder <> ();
  private final CountryIdResolver countryIdResolver;
  private final Set <Id> countryIds = new HashSet <> ();
  private String fileName;
  private StreamParser streamParser;
  private String continentName;
  private int reinforcementBonus;
  private Collection <String> countryNames;

  public ContinentModelDataLoader (final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    this.countryIdResolver = countryIdResolver;
  }

  @Override
  protected ImmutableBiMap <Id, Continent> finalizeData ()
  {
    streamParser.verifyEndOfFile ();
    streamParser.close ();

    return continentsBuilder.build ();
  }

  @Override
  protected void initializeData (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    streamParser = ModelStreamParserFactory.create (fileName);
    this.fileName = fileName;
  }

  @Override
  protected boolean readData ()
  {
    continentName = streamParser.getNextQuotedString ();
    reinforcementBonus = streamParser.getNextInteger ();
    countryNames = streamParser.getNextRemainingQuotedStringsOnLine ();

    return !streamParser.isEndOfFile ();
  }

  @Override
  protected void saveData ()
  {
    countryIds.clear ();

    for (final String countryName : countryNames)
    {
      if (!countryIdResolver.has (countryName))
      {
        log.warn ("Non-existent country [{}] is listed as part of continent [{}] in file [{}].", countryName,
                  continentName, fileName);
        continue;
      }

      countryIds.add (countryIdResolver.getIdOf (countryName));
    }

    final Continent continent = ContinentFactory.create (continentName, reinforcementBonus,
                                                         ImmutableSet.copyOf (countryIds));

    log.debug ("Successfully loaded [{}] with countries [{}] from file [{}].", continent, countryNames, fileName);

    continentsBuilder.put (continent.getId (), continent);
  }
}
