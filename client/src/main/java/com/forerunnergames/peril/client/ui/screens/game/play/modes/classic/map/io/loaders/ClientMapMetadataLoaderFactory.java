package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.io.CompositeMapMetadataLoader;
import com.forerunnergames.peril.common.map.io.ExternalMapMetadataLoader;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.peril.common.map.io.MapMetadataLoader;
import com.forerunnergames.peril.common.map.io.MapMetadataLoaderFactory;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class ClientMapMetadataLoaderFactory implements MapMetadataLoaderFactory
{
  private final MapDataPathParser mapDataPathParser;

  public ClientMapMetadataLoaderFactory (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    mapDataPathParser = new AbsoluteMapResourcesPathParser (gameMode);
  }

  @Override
  public MapMetadataLoader create (final MapType... mapTypes)
  {
    Arguments.checkIsNotNullOrEmpty (mapTypes, "mapTypes");
    Arguments.checkHasNoNullElements (mapTypes, "mapTypes");

    final ImmutableSet.Builder <MapMetadataLoader> mapMetaDataLoadersBuilder = ImmutableSet.builder ();

    for (final MapType mapType : mapTypes)
    {
      mapMetaDataLoadersBuilder.add (new ExternalMapMetadataLoader (mapType, mapDataPathParser));
    }

    return new CompositeMapMetadataLoader (mapMetaDataLoadersBuilder.build ());
  }
}
