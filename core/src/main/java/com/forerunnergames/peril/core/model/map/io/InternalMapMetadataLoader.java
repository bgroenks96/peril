package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.map.DefaultMapMetadata;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.common.map.io.MapMetadataLoader;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
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
    final String internalMapsDirectory = mapDataPathParser.parseMapTypePath (mapType);
    final ImmutableList <String> rawMapDirectoryNames;

    try
    {
      rawMapDirectoryNames = Resources.getJarChildDirectoryNames (InternalMapMetadataLoader.class,
                                                                  internalMapsDirectory);
    }
    catch (final RuntimeException e)
    {
      throw new PlayMapLoadingException (e);
    }

    if (rawMapDirectoryNames.isEmpty ())
    {
      log.warn ("Could not find any maps in [{}].", internalMapsDirectory);
      return ImmutableSet.of ();
    }

    final Set <MapMetadata> mapMetadatas = new HashSet <> ();

    for (final String rawMapDirectoryName : rawMapDirectoryNames)
    {
      final String finalMapName = rawMapDirectoryName.replaceAll ("_", " ");

      if (!GameSettings.isValidMapName (finalMapName)) mapNameError ("Invalid", finalMapName, internalMapsDirectory);

      final MapMetadata mapMetadata = new DefaultMapMetadata (finalMapName, MapType.STOCK,
              mapDataPathParser.getGameMode ());

      if (!mapMetadatas.add (mapMetadata)) mapNameError ("Duplicate", finalMapName, internalMapsDirectory);
    }

    if (mapMetadatas.isEmpty ()) log.warn ("Could not find any maps in [{}].", internalMapsDirectory);

    return ImmutableSet.copyOf (mapMetadatas);
  }

  private void mapNameError (final String prependedMessage, final String mapName, final String internalMapsDirectory)
  {
    throw new PlayMapLoadingException (
            Strings.format ("{} {} map name \'{}\'\n\nLocation:\n\n{}", prependedMessage,
                            mapType.name ().toLowerCase (), Strings.toProperCase (mapName), internalMapsDirectory));
  }
}
