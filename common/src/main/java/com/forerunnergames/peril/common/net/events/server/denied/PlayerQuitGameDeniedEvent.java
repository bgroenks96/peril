package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerQuitGameDeniedEvent.Reason;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerQuitGameDeniedEvent extends AbstractDeniedEvent <PlayerQuitGameRequestEvent, Reason>
{
  public enum Reason
  {
    PLAYER_DOES_NOT_EXIST
  }

  public PlayerQuitGameDeniedEvent (final PlayerQuitGameRequestEvent deniedRequest, final Reason reason)
  {
    super (deniedRequest, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerQuitGameDeniedEvent ()
  {
  }
}
