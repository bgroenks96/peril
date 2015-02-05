package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerLeaveGameDeniedEvent extends AbstractDeniedEvent <PlayerLeaveGameDeniedEvent.REASON>
{
  public enum REASON
  {
    PLAYER_DOES_NOT_EXIST
  }

  public PlayerLeaveGameDeniedEvent (final REASON reason)
  {
    super (reason);
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | %2$s", ((Object) this).getClass ().getSimpleName (), super.toString ());
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameDeniedEvent ()
  {
    super ();
  }
}
