package com.forerunnergames.peril.common.net.packets.card;

import com.forerunnergames.peril.common.net.packets.AssetPacket;

public interface CardPacket extends AssetPacket
{
  int getType ();

  boolean typeIs (final int type);
}
