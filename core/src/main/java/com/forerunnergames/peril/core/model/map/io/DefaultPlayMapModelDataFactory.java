package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;

import com.google.common.collect.ImmutableBiMap;
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

    final CountryModelDataLoader loader =
            PlayMapDataLoadersFactory.createCountryModelDataLoader (mapMetadata.getType ());

    return ImmutableSet.copyOf (loader.load (mapDataPathParser.parseCountriesFileNamePath (mapMetadata)).values ());
  }

  @Override
  public ImmutableSet <Continent> createContinents (final MapMetadata mapMetadata,
                                                    final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    final ContinentModelDataLoader loader =
            PlayMapDataLoadersFactory.createContinentModelDataLoader (mapMetadata.getType (), countryIdResolver);

    return ImmutableSet.copyOf (loader.load (mapDataPathParser.parseContinentsFileNamePath (mapMetadata)).values ());
  }

  @Override
  public GraphModel <Country> createCountryGraph (final MapMetadata mapMetadata, final ImmutableSet <Country> countries)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (countries, "countries");
    Arguments.checkHasNoNullElements (countries, "countries");

    final CountryMapGraphDataLoader loader =
            PlayMapDataLoadersFactory.createCountryMapGraphDataLoader (mapMetadata.getType (), countries);

    final ImmutableBiMap <Country, Iterable <Country>> adjListData =
            loader.load (mapDataPathParser.parseCountryGraphFileNamePath (mapMetadata));

    return DefaultGraphModel.from (adjListData);
  }

  @Override
  public GraphModel <Continent> createContinentGraph (final MapMetadata mapMetadata,
                                                      final ImmutableSet <Continent> continents)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (continents, "continents");
    Arguments.checkHasNoNullElements (continents, "continents");

    final ContinentMapGraphDataLoader loader =
            PlayMapDataLoadersFactory.createContinentMapGraphDataLoader (mapMetadata.getType (), continents);

    final ImmutableBiMap <Continent, Iterable <Continent>> adjListData =
            loader.load (mapDataPathParser.parseCountryGraphFileNamePath (mapMetadata));

    return DefaultGraphModel.from (adjListData);
  }
}
