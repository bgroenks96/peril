/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.playmap.io;

import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.PlayMapDataPathParser;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphDataLoader;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphDataLoader;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultPlayMapModelDataFactory implements PlayMapModelDataFactory
{
  private final PlayMapDataPathParser playMapDataPathParser;

  public DefaultPlayMapModelDataFactory (final PlayMapDataPathParser playMapDataPathParser)
  {
    Arguments.checkIsNotNull (playMapDataPathParser, "playMapDataPathParser");

    this.playMapDataPathParser = playMapDataPathParser;
  }

  @Override
  public CountryFactory createCountries (final PlayMapMetadata playMapMetadata)
  {
    final CountryModelDataLoader countryDataLoader = PlayMapDataLoadersFactory
            .createCountryModelDataLoader (playMapMetadata.getType ());

    return countryDataLoader.load (playMapDataPathParser.parseCountriesFileNamePath (playMapMetadata));
  }

  @Override
  public ContinentFactory createContinents (final PlayMapMetadata playMapMetadata,
                                            final CountryIdResolver countryIdResolver)
  {
    final ContinentModelDataLoader continentDataLoader = PlayMapDataLoadersFactory
            .createContinentModelDataLoader (playMapMetadata.getType (), countryIdResolver);

    return continentDataLoader.load (playMapDataPathParser.parseContinentsFileNamePath (playMapMetadata));
  }

  @Override
  public CountryGraphModel createCountryGraphModel (final PlayMapMetadata playMapMetadata,
                                                    final CountryFactory countryFactory)
  {
    final CountryGraphDataLoader countryGraphDataLoader = PlayMapDataLoadersFactory
            .createCountryGraphDataLoader (playMapMetadata.getType (), countryFactory);

    return countryGraphDataLoader.load (playMapDataPathParser.parseCountryGraphFileNamePath (playMapMetadata));
  }

  @Override
  public ContinentGraphModel createContinentGraphModel (final PlayMapMetadata playMapMetadata,
                                                        final ContinentFactory continentFactory,
                                                        final CountryGraphModel countryGraphModel)
  {
    final ContinentGraphDataLoader continentGraphDataLoader = PlayMapDataLoadersFactory
            .createContinentGraphDataLoader (playMapMetadata.getType (), continentFactory, countryGraphModel);

    return continentGraphDataLoader.load (playMapDataPathParser.parseContinentGraphFileNamePath (playMapMetadata));
  }
}
