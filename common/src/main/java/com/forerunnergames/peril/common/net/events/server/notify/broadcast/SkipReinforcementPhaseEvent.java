package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public class SkipReinforcementPhaseEvent extends AbstractPlayerEvent implements BroadcastNotificationEvent
{
  public enum Reason
  {
    COUNTRY_ARMY_OVERFLOW
  }

  private final Reason reason;

  public SkipReinforcementPhaseEvent (final PlayerPacket player, final Reason reason)
  {
    super (player);

    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  public Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Reason: {}", super.toString (), reason);
  }

  @RequiredForNetworkSerialization
  private SkipReinforcementPhaseEvent ()
  {
    reason = null;
  }
}
