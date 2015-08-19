package com.forerunnergames.peril.core.shared.map.io;

import com.forerunnergames.peril.core.shared.map.MapMetadata;

import com.google.common.collect.ImmutableSet;

public interface MapMetadataLoader
{
  ImmutableSet <MapMetadata> load ();
}
