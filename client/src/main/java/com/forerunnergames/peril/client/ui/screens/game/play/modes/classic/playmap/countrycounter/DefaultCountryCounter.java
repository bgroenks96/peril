/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.countrycounter;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.loaders.CountryNamesDataLoader;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.PlayMapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParserException;

public final class DefaultCountryCounter implements CountryCounter
{
  private final CountryNamesDataLoader loader;
  private final PlayMapDataPathParser pathParser;

  public DefaultCountryCounter (final CountryNamesDataLoader loader, final PlayMapDataPathParser pathParser)
  {
    Arguments.checkIsNotNull (loader, "loader");
    Arguments.checkIsNotNull (pathParser, "pathParser");

    this.loader = loader;
    this.pathParser = pathParser;
  }

  @Override
  public int count (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    if (playMapMetadata.equals (PlayMapMetadata.NULL)) return 0;

    try
    {
      return loader.load (pathParser.parseCountriesFileNamePath (playMapMetadata)).size ();
    }
    catch (final StreamParserException e)
    {
      throw new PlayMapLoadingException (e);
    }
  }
}
