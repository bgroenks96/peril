package com.forerunnergames.peril.core.shared.map.io;

import com.forerunnergames.peril.core.shared.map.MapType;

public interface MapMetadataLoaderFactory
{
  MapMetadataLoader create (final MapType... mapTypes);
}
