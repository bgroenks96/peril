package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerDefendCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseDeniedEvent;

public class PlayerDefendCountryResponseDeniedEvent extends AbstractDeniedEvent <Reason>
        implements ResponseDeniedEvent <Reason>
{
  private final PlayerPacket player;

  public PlayerDefendCountryResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public enum Reason
  {
    INVALID_DIE_COUNT
  }
}
