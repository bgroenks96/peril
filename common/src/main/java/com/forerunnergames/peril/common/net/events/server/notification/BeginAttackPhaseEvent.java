package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class BeginAttackPhaseEvent extends AbstractPlayerEvent implements PlayerNotificationEvent
{
  public BeginAttackPhaseEvent (final PlayerPacket currentPlayer)
  {
    super (currentPlayer);
  }

  @RequiredForNetworkSerialization
  private BeginAttackPhaseEvent ()
  {
  }
}
