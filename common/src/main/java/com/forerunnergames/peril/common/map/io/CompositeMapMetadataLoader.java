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

package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class CompositeMapMetadataLoader implements MapMetadataLoader
{
  private final ImmutableSet <MapMetadataLoader> loaders;

  public CompositeMapMetadataLoader (final ImmutableSet <MapMetadataLoader> loaders)
  {
    Arguments.checkIsNotNull (loaders, "loaders");
    Arguments.checkHasNoNullElements (loaders, "loaders");

    this.loaders = loaders;
  }

  @Override
  public ImmutableSet <MapMetadata> load ()
  {
    final ImmutableSet.Builder <MapMetadata> mapMetaDataBuilder = ImmutableSet.builder ();

    for (final MapMetadataLoader loader : loaders)
    {
      mapMetaDataBuilder.addAll (loader.load ());
    }

    final ImmutableSet <MapMetadata> mapMetadatas = mapMetaDataBuilder.build ();

    if (mapMetadatas.isEmpty ()) throw new PlayMapLoadingException ("Could not find any maps.");

    return mapMetadatas;
  }
}
