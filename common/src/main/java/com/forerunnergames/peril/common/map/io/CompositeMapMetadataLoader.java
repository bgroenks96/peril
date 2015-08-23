package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class CompositeMapMetadataLoader implements MapMetadataLoader
{
  private final ImmutableSet <MapMetadataLoader> loaders;

  public CompositeMapMetadataLoader (final ImmutableSet <MapMetadataLoader> loaders)
  {
    Arguments.checkIsNotNull (loaders, "loaders");
    Arguments.checkHasNoNullElements (loaders, "loaders");

    this.loaders = loaders;
  }

  @Override
  public ImmutableSet <MapMetadata> load ()
  {
    final ImmutableSet.Builder <MapMetadata> mapMetaDataBuilder = ImmutableSet.builder ();

    for (final MapMetadataLoader loader : loaders)
    {
      mapMetaDataBuilder.addAll (loader.load ());
    }

    return mapMetaDataBuilder.build ();
  }
}
