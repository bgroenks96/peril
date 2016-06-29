package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.EndPlayerTurnDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class EndPlayerTurnDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
{
  public enum Reason
  {
    NOT_IN_TURN
  }

  public EndPlayerTurnDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, reason);
  }

  @RequiredForNetworkSerialization
  private EndPlayerTurnDeniedEvent ()
  {
  }
}
