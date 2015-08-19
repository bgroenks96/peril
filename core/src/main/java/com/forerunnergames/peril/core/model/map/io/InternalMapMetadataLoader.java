package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.shared.map.DefaultMapMetadata;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.MapType;
import com.forerunnergames.peril.core.shared.map.io.MapDataPathParser;
import com.forerunnergames.peril.core.shared.map.io.MapMetadataLoader;
import com.forerunnergames.peril.core.shared.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.Resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InternalMapMetadataLoader implements MapMetadataLoader
{
  private static final Logger log = LoggerFactory.getLogger (InternalMapMetadataLoader.class);
  private final MapType mapType;
  private final MapDataPathParser mapDataPathParser;

  public InternalMapMetadataLoader (final MapType mapType, final MapDataPathParser mapDataPathParser)
  {
    Arguments.checkIsNotNull (mapType, "mapType");
    Arguments.checkIsNotNull (mapDataPathParser, "mapDataPathParser");

    this.mapType = mapType;
    this.mapDataPathParser = mapDataPathParser;
  }

  @Override
  public ImmutableSet <MapMetadata> load ()
  {
    final ImmutableList <String> rawMapDirectoryNames = Resources
            .getJarChildDirectoryNames (InternalMapMetadataLoader.class, mapDataPathParser.parseMapTypePath (mapType));

    final Set <MapMetadata> mapMetadatas = new HashSet <> ();

    for (final String rawMapDirectoryName : rawMapDirectoryNames)
    {
      final String finalMapName = rawMapDirectoryName.replaceAll ("_", " ");

      if (!GameSettings.isValidMapName (finalMapName))
      {
        log.warn ("Invalid stock map name detected [{}], ignoring...", finalMapName);
        continue;
      }

      final MapMetadata mapMetadata = new DefaultMapMetadata (finalMapName, MapType.STOCK,
              mapDataPathParser.getGameMode ());

      if (!mapMetadatas.add (mapMetadata))
      {
        log.warn ("Duplicate stock map name detected [{}], ignoring...", finalMapName);
        continue;
      }
    }

    return ImmutableSet.copyOf (mapMetadatas);
  }
}
