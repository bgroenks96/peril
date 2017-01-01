package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerEndTurnDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerEndTurnDeniedEvent extends AbstractPlayerDeniedEvent <PlayerEndTurnRequestEvent, Reason>
{
  public enum Reason
  {
    NOT_IN_TURN,
    ACTION_REQUIRED
  }

  public PlayerEndTurnDeniedEvent (final PlayerPacket player,
                                   final PlayerEndTurnRequestEvent deniedRequest,
                                   final Reason reason)
  {
    super (player, deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerEndTurnDeniedEvent ()
  {
  }
}
