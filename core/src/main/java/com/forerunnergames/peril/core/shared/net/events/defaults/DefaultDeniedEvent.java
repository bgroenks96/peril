package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

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
