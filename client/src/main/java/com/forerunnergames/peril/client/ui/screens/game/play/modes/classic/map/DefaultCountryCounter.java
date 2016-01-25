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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryNamesDataLoader;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParserException;

public final class DefaultCountryCounter implements CountryCounter
{
  private final CountryNamesDataLoader loader;
  private final MapDataPathParser pathParser;

  public DefaultCountryCounter (final CountryNamesDataLoader loader, final MapDataPathParser pathParser)
  {
    Arguments.checkIsNotNull (loader, "loader");
    Arguments.checkIsNotNull (pathParser, "pathParser");

    this.loader = loader;
    this.pathParser = pathParser;
  }

  @Override
  public int count (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return 0;

    try
    {
      return loader.load (pathParser.parseCountriesFileNamePath (mapMetadata)).size ();
    }
    catch (final StreamParserException e)
    {
      throw new PlayMapLoadingException (e);
    }
  }
}
