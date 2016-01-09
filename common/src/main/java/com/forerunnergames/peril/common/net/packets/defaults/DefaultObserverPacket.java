package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.person.AbstractPersonPacket;
import com.forerunnergames.peril.common.net.packets.person.ObserverPacket;

import java.util.UUID;

public final class DefaultObserverPacket extends AbstractPersonPacket implements ObserverPacket
{
  public DefaultObserverPacket (final String name, final UUID id)
  {
    super (name, id);
  }
}
