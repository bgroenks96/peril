package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent.Reason;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerOccupyCountryResponseDeniedEvent extends AbstractDeniedEvent <Reason>
{
  public PlayerOccupyCountryResponseDeniedEvent (final Reason reason)
  {
    super (reason);
  }

  public enum Reason
  {
    DELTA_ARMY_COUNT_BELOW_MIN,
    DELTA_ARMY_COUNT_EXCEEDS_MAX;
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseDeniedEvent ()
  {
    super (null);
  }
}
