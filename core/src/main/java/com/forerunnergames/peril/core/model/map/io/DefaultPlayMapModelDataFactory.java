package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphDataLoader;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphDataLoader;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultPlayMapModelDataFactory implements PlayMapModelDataFactory
{
  private final MapDataPathParser mapDataPathParser;

  public DefaultPlayMapModelDataFactory (final MapDataPathParser mapDataPathParser)
  {
    Arguments.checkIsNotNull (mapDataPathParser, "mapDataPathParser");

    this.mapDataPathParser = mapDataPathParser;
  }

  @Override
  public CountryFactory createCountries (final MapMetadata mapMetadata)
  {
    final CountryModelDataLoader countryDataLoader = PlayMapDataLoadersFactory
            .createCountryModelDataLoader (mapMetadata.getType ());

    return countryDataLoader.load (mapDataPathParser.parseCountriesFileNamePath (mapMetadata));
  }

  @Override
  public ContinentFactory createContinents (final MapMetadata mapMetadata, final CountryIdResolver countryIdResolver)
  {
    final ContinentModelDataLoader continentDataLoader = PlayMapDataLoadersFactory
            .createContinentModelDataLoader (mapMetadata.getType (), countryIdResolver);

    return continentDataLoader.load (mapDataPathParser.parseContinentsFileNamePath (mapMetadata));
  }

  @Override
  public CountryMapGraphModel createCountryMapGraphModel (final MapMetadata mapMetadata,
                                                          final CountryFactory countryFactory)
  {
    final CountryMapGraphDataLoader countryMapGraphDataLoader = PlayMapDataLoadersFactory
            .createCountryMapGraphDataLoader (mapMetadata.getType (), countryFactory);

    return countryMapGraphDataLoader.load (mapDataPathParser.parseCountryGraphFileNamePath (mapMetadata));
  }

  @Override
  public ContinentMapGraphModel createContinentMapGraphModel (final MapMetadata mapMetadata,
                                                              final ContinentFactory continentFactory,
                                                              final CountryMapGraphModel countryMapGraphModel)
  {
    final ContinentMapGraphDataLoader continentMapGraphDataLoader = PlayMapDataLoadersFactory
            .createContinentMapGraphDataLoader (mapMetadata.getType (), continentFactory, countryMapGraphModel);

    return continentMapGraphDataLoader.load (mapDataPathParser.parseContinentGraphFileNamePath (mapMetadata));
  }
}
