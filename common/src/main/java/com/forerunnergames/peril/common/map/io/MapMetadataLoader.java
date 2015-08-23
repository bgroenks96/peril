package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableSet;

public interface MapMetadataLoader
{
  ImmutableSet <MapMetadata> load ();
}
