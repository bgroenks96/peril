package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerRejoinGameDeniedEvent.Reason;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerRejoinGameDeniedEvent extends AbstractDeniedEvent <Reason>
{
  public enum Reason
  {
    PLAYER_NOT_IN_GAME,
    INVALID_ID,
    INVALID_ADDRESS
  }

  public PlayerRejoinGameDeniedEvent (final Reason reason)
  {
    super (reason);
  }

  @RequiredForNetworkSerialization
  private PlayerRejoinGameDeniedEvent ()
  {
  }
}
