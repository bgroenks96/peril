package com.forerunnergames.peril.core.shared.net.packets;

import java.util.UUID;

/**
 *
 * All implementations should extend AbstractGamePacket or override getPacketId, equals, and hashCode appropriately.
 */
public interface AssetPacket
{
  String getName ();

  boolean doesNotHaveId (final UUID id);

  boolean doesNotHaveName (final String name);

  boolean hasName (final String name);

  boolean hasId (final UUID id);

  boolean is (final AssetPacket asset);

  boolean isNot (final AssetPacket asset);

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}
