package com.forerunnergames.peril.core.shared.map.io;

import com.forerunnergames.peril.core.shared.map.DefaultMapMetadata;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.MapType;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.peril.core.shared.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalMapMetadataLoader implements MapMetadataLoader
{
  protected final Logger log = LoggerFactory.getLogger (getClass ());
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
    final Set <MapMetadata> metadatas = new HashSet <> ();
    final File externalMapsDirectory = new File (mapDataPathParser.parseMapTypePath (mapType));
    final File[] childPathFiles = externalMapsDirectory.listFiles ();

    if (childPathFiles == null)
    {
      throw new PlayMapLoadingException (Strings.format ("Could not find any maps in [{}].", externalMapsDirectory));
    }

    for (final File childPathFile : childPathFiles)
    {
      if (!childPathFile.isDirectory ()) continue;

      final String rawMapDirectoryName = childPathFile.getName ();
      final String finalMapName = rawMapDirectoryName.replaceAll ("_", " ");

      if (!GameSettings.isValidMapName (finalMapName))
      {
        log.warn ("Invalid {} map name detected [{}], ignoring...", mapType.name (), finalMapName);
        continue;
      }

      final MapMetadata mapMetaData = new DefaultMapMetadata (finalMapName, mapType, mapDataPathParser.getGameMode ());

      if (!metadatas.add (mapMetaData))
      {
        log.warn ("Duplicate {} map name detected [{}], ignoring...", mapType.name (), finalMapName);
      }
    }

    if (metadatas.isEmpty ())
    {
      throw new PlayMapLoadingException (Strings.format ("Could not find any maps in [{}].", externalMapsDirectory));
    }

    return ImmutableSet.copyOf (metadatas);
  }
}
