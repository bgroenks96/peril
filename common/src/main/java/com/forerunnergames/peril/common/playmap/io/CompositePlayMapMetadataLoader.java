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

package com.forerunnergames.peril.common.playmap.io;

import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class CompositePlayMapMetadataLoader implements PlayMapMetadataLoader
{
  private final ImmutableSet <PlayMapMetadataLoader> loaders;

  public CompositePlayMapMetadataLoader (final ImmutableSet <PlayMapMetadataLoader> loaders)
  {
    Arguments.checkIsNotNull (loaders, "loaders");
    Arguments.checkHasNoNullElements (loaders, "loaders");

    this.loaders = loaders;
  }

  @Override
  public ImmutableSet <PlayMapMetadata> load ()
  {
    final ImmutableSet.Builder <PlayMapMetadata> playMapMetaDataBuilder = ImmutableSet.builder ();

    for (final PlayMapMetadataLoader loader : loaders)
    {
      playMapMetaDataBuilder.addAll (loader.load ());
    }

    final ImmutableSet <PlayMapMetadata> playMapMetadatas = playMapMetaDataBuilder.build ();

    if (playMapMetadatas.isEmpty ()) throw new PlayMapLoadingException ("Could not find any maps.");

    return playMapMetadatas;
  }
}
