package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;

public interface MapMetadataFinder
{
  MapMetadata find (final String mapName);
}
