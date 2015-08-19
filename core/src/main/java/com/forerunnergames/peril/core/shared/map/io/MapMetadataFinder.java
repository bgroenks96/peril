package com.forerunnergames.peril.core.shared.map.io;

import com.forerunnergames.peril.core.shared.map.MapMetadata;

public interface MapMetadataFinder
{
  MapMetadata find (final String mapName);
}
