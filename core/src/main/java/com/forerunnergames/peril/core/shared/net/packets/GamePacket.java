package com.forerunnergames.peril.core.shared.net.packets;

/**
 *
 * All implementations should extend AbstractGamePacket or override getPacketId, equals, and hashCode appropriately.
 */
public interface GamePacket
{
  boolean is (final GamePacket packet);

  boolean isNot (final GamePacket packet);

  int getPacketId ();

  @Override
  boolean equals (Object obj);

  @Override
  int hashCode ();
}
