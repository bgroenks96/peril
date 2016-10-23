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

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public final class DefaultPlayMapMetadataFinder implements PlayMapMetadataFinder
{
  private final GameMode gameMode;
  private final ImmutableSet <PlayMapMetadata> playMapMetadatas;

  public DefaultPlayMapMetadataFinder (final GameMode gameMode, final ImmutableSet <PlayMapMetadata> playMapMetadatas)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (playMapMetadatas, "playMapMetadatas");
    Arguments.checkHasNoNullElements (playMapMetadatas, "playMapMetadatas");

    this.gameMode = gameMode;
    this.playMapMetadatas = playMapMetadatas;
  }

  @Override
  public PlayMapMetadata find (final String playMapName)
  {
    Arguments.checkIsNotNull (playMapName, "playMapName");

    for (final PlayMapMetadata playMapMetadata : playMapMetadatas)
    {
      if (playMapMetadata.getName ().equalsIgnoreCase (playMapName) && playMapMetadata.getMode () == gameMode) return playMapMetadata;
    }

    throw new PlayMapLoadingException (Strings.format ("Cannot find any map named [{}] for {}: [{}] in {} set:\n\n{}",
                                                       playMapName, gameMode.getClass ().getSimpleName (), gameMode,
                                                       PlayMapMetadata.class.getSimpleName (), playMapMetadatas));
  }
}
