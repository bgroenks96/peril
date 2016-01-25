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
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;

public interface PlayMapModelDataFactory
{
  CountryFactory createCountries (final MapMetadata mapMetadata);

  ContinentFactory createContinents (final MapMetadata mapMetadata, final CountryIdResolver countryIdResolver);

  CountryMapGraphModel createCountryMapGraphModel (final MapMetadata mapMetadata, final CountryFactory countryFactory);

  ContinentMapGraphModel createContinentMapGraphModel (final MapMetadata mapMetadata,
                                                       final ContinentFactory continentFactory,
                                                       final CountryMapGraphModel countryMapGraphModel);
}
