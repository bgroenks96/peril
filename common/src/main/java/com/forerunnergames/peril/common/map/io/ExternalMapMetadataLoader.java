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

import com.forerunnergames.peril.common.map.DefaultMapMetadata;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExternalMapMetadataLoader implements MapMetadataLoader
{
  private static final Logger log = LoggerFactory.getLogger (ExternalMapMetadataLoader.class);
  private final MapType mapType;
  private final MapDataPathParser mapDataPathParser;

  public ExternalMapMetadataLoader (final MapType mapType, final MapDataPathParser mapDataPathParser)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (mapDataPathParser, "mapDataPathParser");

    this.mapType = mapType;
    this.mapDataPathParser = mapDataPathParser;
  }

  @Override
  public ImmutableSet <MapMetadata> load ()
  {
    final Set <MapMetadata> mapMetadatas = new HashSet<> ();
    final File externalMapsDirectory = new File (mapDataPathParser.parseMapTypePath (mapType));
    final File[] childPathFiles = externalMapsDirectory.listFiles ();

    if (childPathFiles == null)
    {
      log.warn ("Could not find any maps in [{}].", externalMapsDirectory);
      return ImmutableSet.of ();
    }

    for (final File childPathFile : childPathFiles)
    {
      if (!childPathFile.isDirectory ()) continue;

      final String rawMapDirectoryName = childPathFile.getName ();
      final String finalMapName = rawMapDirectoryName.replaceAll ("_", " ");

      if (!GameSettings.isValidMapName (finalMapName)) mapNameError ("Invalid", finalMapName, externalMapsDirectory);

      final MapMetadata mapMetaData = new DefaultMapMetadata (finalMapName, mapType, mapDataPathParser.getGameMode ());

      if (!mapMetadatas.add (mapMetaData)) mapNameError ("Duplicate", finalMapName, externalMapsDirectory);
    }

    if (mapMetadatas.isEmpty ()) log.warn ("Could not find any maps in [{}].", externalMapsDirectory);

    return ImmutableSet.copyOf (mapMetadatas);
  }

  private void mapNameError (final String prependedMessage, final String mapName, final File externalMapsDirectory)
  {
    throw new PlayMapLoadingException (Strings.format ("{} {} map name \'{}\'\n\nLocation:\n\n{}", prependedMessage,
                                                       mapType.name ().toLowerCase (), Strings.toProperCase (mapName),
                                                       externalMapsDirectory.getAbsolutePath ()));
  }
}
