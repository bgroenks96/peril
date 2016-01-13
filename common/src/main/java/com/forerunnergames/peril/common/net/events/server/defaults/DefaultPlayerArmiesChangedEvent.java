package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerArmiesChangedEvent extends AbstractPlayerArmiesChangedEvent
{
  public DefaultPlayerArmiesChangedEvent (final PlayerPacket player, @AllowNegative final int deltaArmyCount)
  {
    super (player, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerArmiesChangedEvent ()
  {
  }
}
