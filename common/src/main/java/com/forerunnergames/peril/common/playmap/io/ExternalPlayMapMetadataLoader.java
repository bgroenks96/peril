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

import com.forerunnergames.peril.common.playmap.DefaultPlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapDirectoryType;
import com.forerunnergames.peril.common.playmap.PlayMapLoadingException;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.PlayMapType;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExternalPlayMapMetadataLoader implements PlayMapMetadataLoader
{
  private static final Logger log = LoggerFactory.getLogger (ExternalPlayMapMetadataLoader.class);
  private final PlayMapType playMapType;
  private final PlayMapDataPathParser playMapDataPathParser;

  public ExternalPlayMapMetadataLoader (final PlayMapType playMapType, final PlayMapDataPathParser playMapDataPathParser)
  {
    Arguments.checkIsNotNull (playMapType, "playMapType");
    Arguments.checkIsNotNull (playMapDataPathParser, "playMapDataPathParser");

    this.playMapType = playMapType;
    this.playMapDataPathParser = playMapDataPathParser;
  }

  @Override
  public ImmutableSet <PlayMapMetadata> load ()
  {
    final File externalPlayMapsDirectory = new File (playMapDataPathParser.parsePlayMapTypePath (playMapType));
    final File[] childPathFiles = externalPlayMapsDirectory.listFiles ();

    if (childPathFiles == null)
    {
      log.warn ("Could not find any play maps in [{}].", externalPlayMapsDirectory);
      return ImmutableSet.of ();
    }

    final Set <PlayMapMetadata> metadatas = new HashSet <> ();

    for (final File childPathFile : childPathFiles)
    {
      if (!childPathFile.isDirectory ()) continue;

      final String rawPlayMapDirectoryName = childPathFile.getName ();
      final String finalPlayMapName = Strings.toProperCase (rawPlayMapDirectoryName.replaceAll ("_", " "));

      if (!GameSettings.isValidPlayMapName (finalPlayMapName))
      {
        playMapNameError ("Invalid", finalPlayMapName, externalPlayMapsDirectory);
      }

      final PlayMapMetadata playMapMetaData = new DefaultPlayMapMetadata (finalPlayMapName, playMapType,
              playMapDataPathParser.getGameMode (), rawPlayMapDirectoryName, PlayMapDirectoryType.EXTERNAL);

      if (!metadatas.add (playMapMetaData))
      {
        playMapNameError ("Duplicate", finalPlayMapName, externalPlayMapsDirectory);
      }
    }

    if (metadatas.isEmpty ()) log.warn ("Could not find any play maps in [{}].", externalPlayMapsDirectory);

    return ImmutableSet.copyOf (metadatas);
  }

  private void playMapNameError (final String prependedMessage,
                                 final String playMapName,
                                 final File externalPlayMapsDirectory)
  {
    throw new PlayMapLoadingException (Strings.format ("{} {} map name \'{}\'\n\nLocation:\n\n{}", prependedMessage,
                                                       playMapType.name ().toLowerCase (),
                                                       Strings.toProperCase (playMapName),
                                                       externalPlayMapsDirectory.getAbsolutePath ()));
  }
}
