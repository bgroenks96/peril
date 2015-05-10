package com.forerunnergames.peril.core.shared.net.events.server.defaults;

import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultDeniedEvent extends AbstractDeniedEvent <String>
{
  public DefaultDeniedEvent (final String reason)
  {
    super (reason);
  }

  @RequiredForNetworkSerialization
  private DefaultDeniedEvent ()
  {
  }
}
