package com.forerunnergames.peril.common.map.io;

import com.forerunnergames.peril.common.map.MapType;

public interface MapMetadataLoaderFactory
{
  MapMetadataLoader create (final MapType... mapTypes);
}
