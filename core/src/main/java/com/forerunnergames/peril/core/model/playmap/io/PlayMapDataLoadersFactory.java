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

import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.core.model.io.InternalStreamParserFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphDataLoader;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphDataLoader;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;

public final class PlayMapDataLoadersFactory
{
  public static CountryModelDataLoader createCountryModelDataLoader (final PlayMapType playMapType)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");

    return new CountryModelDataLoader (getStreamParserFactory (playMapType));
  }

  public static ContinentModelDataLoader createContinentModelDataLoader (final PlayMapType playMapType,
                                                                         final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    return new ContinentModelDataLoader (getStreamParserFactory (playMapType), countryIdResolver);
  }

  public static CountryGraphDataLoader createCountryGraphDataLoader (final PlayMapType playMapType,
                                                                     final CountryFactory countries)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");
    Arguments.checkIsNotNull (countries, "countries");

    return new CountryGraphDataLoader (getStreamParserFactory (playMapType), countries);
  }

  public static ContinentGraphDataLoader createContinentGraphDataLoader (final PlayMapType playMapType,
                                                                         final ContinentFactory continents,
                                                                         final CountryGraphModel countryGraphModel)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");
    Arguments.checkIsNotNull (continents, "continents");
    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");

    return new ContinentGraphDataLoader (getStreamParserFactory (playMapType), continents, countryGraphModel);
  }

  private static StreamParserFactory getStreamParserFactory (final PlayMapType playMapType)
  {
    assert playMapType != null;

    final StreamParserFactory streamParserFactory;

    switch (playMapType)
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
                                PlayMapType.class.getSimpleName (), playMapType));
      }
    }

    return streamParserFactory;
  }

  private PlayMapDataLoadersFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
