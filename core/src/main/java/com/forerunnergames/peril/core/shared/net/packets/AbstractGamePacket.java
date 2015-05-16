package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractGamePacket implements GamePacket
{
  private final int packetId;

  protected AbstractGamePacket (final int modelId)
  {
    Arguments.checkIsNotNull (modelId, "modelId");

    packetId = modelId;
  }

  @Override
  public boolean is (final GamePacket packet)
  {
    Arguments.checkIsNotNull (packet, "packet");

    return getPacketId () == packet.getPacketId ();
  }

  @Override
  public boolean isNot (final GamePacket packet)
  {
    Arguments.checkIsNotNull (packet, "packet");

    return !is (packet);
  }

  @Override
  public int getPacketId ()
  {
    return packetId;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof GamePacket)) return false;
    return ((GamePacket) obj).is (this);
  }

  @Override
  public int hashCode ()
  {
    return getPacketId ();
  }

  @RequiredForNetworkSerialization
  private AbstractGamePacket ()
  {
    packetId = 0;
  }
}
