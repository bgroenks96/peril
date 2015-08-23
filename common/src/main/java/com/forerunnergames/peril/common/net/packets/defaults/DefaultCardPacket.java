package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.AbstractAssetPacket;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.tools.common.Arguments;

import java.util.UUID;

public class DefaultCardPacket extends AbstractAssetPacket implements CardPacket
{
  private final int type;

  public DefaultCardPacket (final String name, final int type, final UUID id)
  {
    super (name, id);

    Arguments.checkIsNotNegative (type, "type");

    this.type = type;
  }

  @Override
  public int getType ()
  {
    return type;
  }

  @Override
  public boolean typeIs (final int type)
  {
    return this.type == type;
  }
}
