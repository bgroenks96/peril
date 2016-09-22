package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryResponseDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
        implements PlayerResponseDeniedEvent <Reason>
{
  public enum Reason
  {
    FORTIFY_DELTA_ARMY_COUNT_OVERFLOW,
    FORTIFY_DELTA_ARMY_COUNT_UNDERFLOW,
    PLAYER_NOT_IN_TURN
  }

  public PlayerFortifyCountryResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryResponseDeniedEvent ()
  {
  }
}
