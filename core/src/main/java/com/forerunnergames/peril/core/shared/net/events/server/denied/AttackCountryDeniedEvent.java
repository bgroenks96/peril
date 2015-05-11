package com.forerunnergames.peril.core.shared.net.events.server.denied;

import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.DeniedEvent;

public final class AttackCountryDeniedEvent implements DeniedEvent <String>
{
  private final DeniedEvent <String> deniedEvent;

  public AttackCountryDeniedEvent (final String reason)
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
  private AttackCountryDeniedEvent ()
  {
    deniedEvent = null;
  }
}
