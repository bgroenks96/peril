package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public final class SkipPlayerTurnEvent extends AbstractPlayerEvent implements BroadcastNotificationEvent
{
  public SkipPlayerTurnEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private SkipPlayerTurnEvent ()
  {
  }
}
