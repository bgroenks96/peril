package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.io.MapDataPathParser;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class DefaultPlayMapModelDataFactory implements PlayMapModelDataFactory
{
  private final MapDataPathParser mapDataPathParser;

  public DefaultPlayMapModelDataFactory (final MapDataPathParser mapDataPathParser)
  {
    Arguments.checkIsNotNull (mapDataPathParser, "mapDataPathParser");

    this.mapDataPathParser = mapDataPathParser;
  }

  @Override
  public ImmutableSet <Country> createCountries (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    final CountryModelDataLoader loader = CountryModelDataLoaderFactory.create (mapMetadata.getType ());

    return ImmutableSet.copyOf (loader.load (mapDataPathParser.parseCountriesFileNamePath (mapMetadata)).values ());
  }

  @Override
  public ImmutableSet <Continent> createContinents (final MapMetadata mapMetadata,
                                                    final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    final ContinentModelDataLoader loader = ContinentModelDataLoaderFactory.create (mapMetadata.getType (),
                                                                                    countryIdResolver);

    return ImmutableSet.copyOf (loader.load (mapDataPathParser.parseContinentsFileNamePath (mapMetadata)).values ());
  }
}
