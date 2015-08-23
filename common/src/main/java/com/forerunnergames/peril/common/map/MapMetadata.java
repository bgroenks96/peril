package com.forerunnergames.peril.common.map;

import com.forerunnergames.peril.common.game.GameMode;

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
