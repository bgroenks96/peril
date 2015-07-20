package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseDeniedEvent;

public final class PlayerSelectCountryResponseDeniedEvent extends DefaultPlayerSelectCountryResponseEvent implements
        ResponseDeniedEvent <Reason>
{
  private final Reason reason;

  public PlayerSelectCountryResponseDeniedEvent (final String selectedCountryName, final Reason reason)
  {
    super (selectedCountryName);
    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  public enum Reason
  {
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    COUNTRY_ALREADY_OWNED;
  }

  @Override
  public Reason getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryResponseDeniedEvent ()
  {
    super (null);
    reason = null;
  }
}
