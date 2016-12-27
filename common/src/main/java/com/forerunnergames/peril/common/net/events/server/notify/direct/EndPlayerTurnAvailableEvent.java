package com.forerunnergames.peril.common.net.events.server.notify.direct;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DirectNotificiationEvent;

public final class EndPlayerTurnAvailableEvent extends AbstractPlayerEvent implements DirectNotificiationEvent
{
  public EndPlayerTurnAvailableEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private EndPlayerTurnAvailableEvent ()
  {
  }
}
