/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
