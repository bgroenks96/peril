package com.forerunnergames.peril.core.shared.net.packets;

/**
 *
 * All implementations should extend AbstractGamePacket or override getPacketId, equals, and hashCode appropriately.
 */
public interface AssetPacket
{
  String getName ();

  boolean doesNotHaveId (final int id);

  boolean doesNotHaveName (final String name);

  boolean hasName (final String name);

  boolean hasId (final int id);

  boolean is (final AssetPacket asset);

  boolean isNot (final AssetPacket asset);

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();
}
