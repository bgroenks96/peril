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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.AbsolutePlayMapGraphicsPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.io.pathparsers.PlayMapGraphicsPathParser;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultCountryImageDataRepositoryFactory implements CountryImageDataRepositoryFactory
{
  private final BiMapDataLoader <String, CountryImageData> countryImageDataLoader;

  public DefaultCountryImageDataRepositoryFactory (final BiMapDataLoader <String, CountryImageData> countryImageDataLoader)
  {
    Arguments.checkIsNotNull (countryImageDataLoader, "countryImageDataLoader");

    this.countryImageDataLoader = countryImageDataLoader;
  }

  @Override
  public CountryImageDataRepository create (final PlayMapMetadata playMapMetadata)
  {
    Arguments.checkIsNotNull (playMapMetadata, "playMapMetadata");

    final PlayMapGraphicsPathParser playMapGraphicsPathParser = new AbsolutePlayMapGraphicsPathParser (
            playMapMetadata.getMode ());

    return new DefaultCountryImageDataRepository (countryImageDataLoader
            .load (playMapGraphicsPathParser.parseCountryImageDataFileNamePath (playMapMetadata)));
  }
}
