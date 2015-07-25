package com.forerunnergames.peril.core.shared.net.events.server.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

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
    return Strings.format ("{}: {}", getClass ().getSimpleName (), deniedEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerDeniedEvent ()
  {
    deniedEvent = null;
  }
}
