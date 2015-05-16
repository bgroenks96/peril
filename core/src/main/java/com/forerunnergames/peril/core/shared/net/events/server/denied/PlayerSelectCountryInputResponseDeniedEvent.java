package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerSelectCountryInputResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryInputResponseDeniedEvent.Reason;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputResponseDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputResponseDeniedEvent extends DefaultPlayerSelectCountryInputResponseEvent
        implements InputResponseDeniedEvent <Reason>
{
  public enum Reason
  {
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    COUNTRY_ALREADY_OWNED;
  }

  private final Reason reason;

  public PlayerSelectCountryInputResponseDeniedEvent (final String selectedCountryName, final Reason reason)
  {
    super (selectedCountryName);
    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  @Override
  public Reason getReason ()
  {
    return reason;
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputResponseDeniedEvent ()
  {
    super (null);
    reason = null;
  }
}
