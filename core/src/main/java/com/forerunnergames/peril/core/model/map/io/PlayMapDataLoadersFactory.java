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

import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.core.model.io.InternalStreamParserFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphDataLoader;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphDataLoader;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

public final class PlayMapDataLoadersFactory
{
  public static CountryModelDataLoader createCountryModelDataLoader (final MapType mapType)
  {
    Arguments.checkIsNotNull (mapType, "mapType");

    return new CountryModelDataLoader (getStreamParserFactory (mapType));
  }

  public static ContinentModelDataLoader createContinentModelDataLoader (final MapType mapType,
                                                                         final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    return new ContinentModelDataLoader (getStreamParserFactory (mapType), countryIdResolver);
  }

  public static CountryMapGraphDataLoader createCountryMapGraphDataLoader (final MapType mapType,
                                                                           final CountryFactory countries)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (countries, "countries");

    return new CountryMapGraphDataLoader (getStreamParserFactory (mapType), countries);
  }

  public static ContinentMapGraphDataLoader createContinentMapGraphDataLoader (final MapType mapType,
                                                                               final ContinentFactory continents,
                                                                               final CountryMapGraphModel countryMapGraphModel)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (continents, "continennts");

    return new ContinentMapGraphDataLoader (getStreamParserFactory (mapType), continents, countryMapGraphModel);
  }

  private static StreamParserFactory getStreamParserFactory (final MapType mapType)
  {
    assert mapType != null;

    final StreamParserFactory streamParserFactory;

    switch (mapType)
    {
      case STOCK:
      {
        streamParserFactory = new InternalStreamParserFactory ();
        break;
      }
      case CUSTOM:
      {
        streamParserFactory = new ExternalStreamParserFactory ();
        break;
      }
      default:
      {
        throw new PlayMapLoadingException (
                Strings.format ("Cannot create {} for {}: [{}].", PlayMapDataLoadersFactory.class.getSimpleName (),
                                MapType.class.getSimpleName (), mapType));
      }
    }

    return streamParserFactory;
  }

  private PlayMapDataLoadersFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
