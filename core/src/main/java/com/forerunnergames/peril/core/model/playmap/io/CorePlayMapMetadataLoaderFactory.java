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

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.playmap.io.CompositePlayMapMetadataLoader;
import com.forerunnergames.peril.common.playmap.io.ExternalPlayMapMetadataLoader;
import com.forerunnergames.peril.common.playmap.io.PlayMapDataPathParser;
import com.forerunnergames.peril.common.playmap.io.PlayMapMetadataLoader;
import com.forerunnergames.peril.common.playmap.io.PlayMapMetadataLoaderFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public final class CorePlayMapMetadataLoaderFactory implements PlayMapMetadataLoaderFactory
{
  private final PlayMapDataPathParser playMapDataPathParser;

  public CorePlayMapMetadataLoaderFactory (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    playMapDataPathParser = new CorePlayMapDataPathParser (gameMode);
  }

  @Override
  public PlayMapMetadataLoader create (final PlayMapType... playMapTypes)
  {
    Arguments.checkIsNotNullOrEmpty (playMapTypes, "playMapTypes");
    Arguments.checkHasNoNullElements (playMapTypes, "playMapTypes");

    final ImmutableSet.Builder <PlayMapMetadataLoader> builder = ImmutableSet.builder ();

    for (final PlayMapType playMapType : playMapTypes)
    {
      switch (playMapType)
      {
        case STOCK:
        {
          builder.add (new InternalPlayMapMetadataLoader (playMapType, playMapDataPathParser));
          break;
        }
        case CUSTOM:
        {
          builder.add (new ExternalPlayMapMetadataLoader (playMapType, playMapDataPathParser));
          break;
        }
        default:
        {
          throw new PlayMapLoadingException (
                  Strings.format ("Unsupported {}: [{}].", PlayMapType.class.getSimpleName (), playMapType));
        }
      }
    }

    return new CompositePlayMapMetadataLoader (builder.build ());
  }
}
