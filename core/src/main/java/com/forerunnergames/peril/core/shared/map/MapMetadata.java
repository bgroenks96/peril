package com.forerunnergames.peril.core.shared.map;

import com.forerunnergames.peril.core.model.rules.GameMode;

public interface MapMetadata
{
  MapMetadata NULL_MAP_METADATA = new NullMapMetadata ();

  String getName ();

  MapType getType ();

  GameMode getMode ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}
