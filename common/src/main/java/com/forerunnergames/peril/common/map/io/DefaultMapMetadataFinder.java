package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public final class DefaultMapMetadataFinder implements MapMetadataFinder
{
  private final GameMode gameMode;
  private final ImmutableSet <MapMetadata> mapMetadatas;

  public DefaultMapMetadataFinder (final GameMode gameMode, final ImmutableSet <MapMetadata> mapMetadatas)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");
    Arguments.checkIsNotNull (mapMetadatas, "mapMetadatas");
    Arguments.checkHasNoNullElements (mapMetadatas, "mapMetadatas");

    this.gameMode = gameMode;
    this.mapMetadatas = mapMetadatas;
  }

  @Override
  public MapMetadata find (final String mapName)
  {
    Arguments.checkIsNotNull (mapName, "mapName");

    for (final MapMetadata mapMetadata : mapMetadatas)
    {
      if (mapMetadata.getName ().equalsIgnoreCase (mapName) && mapMetadata.getMode () == gameMode) return mapMetadata;
    }

    throw new PlayMapLoadingException (Strings.format ("Cannot find any map named [{}] for {}: [{}] in {} set:\n\n{}",
                                                       mapName, gameMode.getClass ().getSimpleName (), gameMode,
                                                       MapMetadata.class.getSimpleName (), mapMetadatas));
  }
}
