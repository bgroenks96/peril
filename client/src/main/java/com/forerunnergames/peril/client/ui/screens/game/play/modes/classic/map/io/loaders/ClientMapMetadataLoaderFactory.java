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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.io.CompositeMapMetadataLoader;
import com.forerunnergames.peril.common.map.io.ExternalMapMetadataLoader;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.common.map.io.MapMetadataLoader;
import com.forerunnergames.peril.common.map.io.MapMetadataLoaderFactory;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class ClientMapMetadataLoaderFactory implements MapMetadataLoaderFactory
{
  private final MapDataPathParser mapDataPathParser;

  public ClientMapMetadataLoaderFactory (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    mapDataPathParser = new AbsoluteMapResourcesPathParser (gameMode);
  }

  @Override
  public MapMetadataLoader create (final MapType... mapTypes)
  {
    Arguments.checkIsNotNullOrEmpty (mapTypes, "mapTypes");
    Arguments.checkHasNoNullElements (mapTypes, "mapTypes");

    final ImmutableSet.Builder <MapMetadataLoader> mapMetaDataLoadersBuilder = ImmutableSet.builder ();

    for (final MapType mapType : mapTypes)
    {
      mapMetaDataLoadersBuilder.add (new ExternalMapMetadataLoader (mapType, mapDataPathParser));
    }

    return new CompositeMapMetadataLoader (mapMetaDataLoadersBuilder.build ());
  }
}
