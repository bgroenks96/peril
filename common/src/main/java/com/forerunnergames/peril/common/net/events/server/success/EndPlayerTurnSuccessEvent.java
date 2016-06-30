package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastSuccessEvent;

public final class EndPlayerTurnSuccessEvent extends AbstractPlayerEvent implements BroadcastSuccessEvent
{
  public EndPlayerTurnSuccessEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private EndPlayerTurnSuccessEvent ()
  {
  }
}
