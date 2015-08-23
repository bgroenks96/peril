package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public final class DefendCountryDeniedEvent implements DeniedEvent <String>
{
  private final DeniedEvent <String> deniedEvent;

  public DefendCountryDeniedEvent (final String reason)
  {
    Arguments.checkIsNotNull (reason, "reason");

    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @RequiredForNetworkSerialization
  private DefendCountryDeniedEvent ()
  {
    deniedEvent = null;
  }
}
