package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerLeaveGameDeniedEvent extends AbstractDeniedEvent <PlayerLeaveGameDeniedEvent.Reason>
{
  public PlayerLeaveGameDeniedEvent (final Reason reason)
  {
    super (reason);
  }

  public enum Reason
  {
    PLAYER_DOES_NOT_EXIST
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | %2$s", getClass ().getSimpleName (), super.toString ());
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameDeniedEvent ()
  {
    super ();
  }
}
