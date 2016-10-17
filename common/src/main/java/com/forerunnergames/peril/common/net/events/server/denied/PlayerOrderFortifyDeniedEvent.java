package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderFortifyDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOrderFortifyDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
{
  public enum Reason
  {
    FORTIFY_DELTA_ARMY_COUNT_OVERFLOW,
    FORTIFY_DELTA_ARMY_COUNT_UNDERFLOW,
    PLAYER_NOT_IN_TURN
  }

  public PlayerOrderFortifyDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerOrderFortifyDeniedEvent ()
  {
  }
}
