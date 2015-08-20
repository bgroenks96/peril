package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.shared.game.GameMode;
import com.forerunnergames.peril.core.shared.map.MapType;
import com.forerunnergames.peril.core.shared.map.PlayMapLoadingException;
import com.forerunnergames.peril.core.shared.map.io.CompositeMapMetadataLoader;
import com.forerunnergames.peril.core.shared.map.io.ExternalMapMetadataLoader;
import com.forerunnergames.peril.core.shared.map.io.MapDataPathParser;
import com.forerunnergames.peril.core.shared.map.io.MapMetadataLoader;
import com.forerunnergames.peril.core.shared.map.io.MapMetadataLoaderFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

public final class CoreMapMetadataLoaderFactory implements MapMetadataLoaderFactory
{
  private final MapDataPathParser mapDataPathParser;

  public CoreMapMetadataLoaderFactory (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    mapDataPathParser = new CoreMapDataPathParser (gameMode);
  }

  @Override
  public MapMetadataLoader create (final MapType... mapTypes)
  {
    Arguments.checkIsNotNullOrEmpty (mapTypes, "mapTypes");
    Arguments.checkHasNoNullElements (mapTypes, "mapTypes");

    final ImmutableSet.Builder <MapMetadataLoader> mapMetaDataLoadersBuilder = ImmutableSet.builder ();

    for (final MapType mapType : mapTypes)
    {
      switch (mapType)
      {
        case STOCK:
        {
          mapMetaDataLoadersBuilder.add (new InternalMapMetadataLoader (mapType, mapDataPathParser));
          break;
        }
        case CUSTOM:
        {
          mapMetaDataLoadersBuilder.add (new ExternalMapMetadataLoader (mapType, mapDataPathParser));
          break;
        }
        default:
        {
          throw new PlayMapLoadingException (
                  Strings.format ("Unsupported {}: [{}].", MapType.class.getSimpleName (), mapType));
        }
      }
    }

    return new CompositeMapMetadataLoader (mapMetaDataLoadersBuilder.build ());
  }
}
