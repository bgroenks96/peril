package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractCountryStateChangeDeniedEvent extends AbstractDeniedEvent <Reason>
        implements PlayerDeniedEvent <Reason>
{
  public enum Reason
  {
    COUNTRY_ARMY_COUNT_OVERFLOW,
    COUNTRY_ARMY_COUNT_UNDERFLOW,
    DELTA_ARMY_COUNT_UNDERFLOW,
    DELTA_ARMY_COUNT_OVERFLOW,
    NOT_OWNER_OF_COUNTRY,
    COUNTRY_UNAVAILABLE,
    COUNTRY_DOES_NOT_EXIST,
    INSUFFICIENT_ARMIES_IN_HAND,
    REINFORCEMENT_NOT_ALLOWED,
    COUNTRY_ALREADY_OWNED,
    COUNTRY_DISABLED;
  }

  protected AbstractCountryStateChangeDeniedEvent (final Reason reason)
  {
    super (reason);
  }

  @RequiredForNetworkSerialization
  protected AbstractCountryStateChangeDeniedEvent ()
  {
  }
}
