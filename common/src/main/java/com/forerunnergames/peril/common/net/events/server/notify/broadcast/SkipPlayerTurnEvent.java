package com.forerunnergames.peril.common.net.events.server.notify.broadcast;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.BroadcastNotificationEvent;

public final class SkipPlayerTurnEvent extends AbstractPlayerEvent implements BroadcastNotificationEvent
{
  public enum Reason
  {
    NO_INPUT_REQUIRED,
    PLAYER_INPUT_TIMED_OUT
  }

  private final Reason reason;

  public SkipPlayerTurnEvent (final PlayerPacket player, final Reason reason)
  {
    super (player);

    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  public Reason getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  private SkipPlayerTurnEvent ()
  {
    reason = null;
  }
}
