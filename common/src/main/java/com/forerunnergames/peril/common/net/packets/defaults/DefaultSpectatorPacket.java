package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.person.AbstractPersonPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;

import java.util.UUID;

public final class DefaultSpectatorPacket extends AbstractPersonPacket implements SpectatorPacket
{
  public DefaultSpectatorPacket (final String name, final UUID id)
  {
    super (name, id);
  }
}
