package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.DeniedEvent;

public final class DefaultPlayerDeniedEvent implements PlayerDeniedEvent <String>
{
  private final DeniedEvent <String> deniedEvent;

  public DefaultPlayerDeniedEvent (final String reason)
  {
    Arguments.checkIsNotNull (reason, "reason");

    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason ()
  {
    return deniedEvent.getReason ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s", deniedEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerDeniedEvent ()
  {
    deniedEvent = null;
  }
}
