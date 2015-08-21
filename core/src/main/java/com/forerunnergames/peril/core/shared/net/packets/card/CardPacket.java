package com.forerunnergames.peril.core.shared.net.packets.card;

import com.forerunnergames.peril.core.shared.net.packets.AssetPacket;

public interface CardPacket extends AssetPacket
{
  int getType ();

  boolean typeIs (final int type);
}
