package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;

public final class DefaultDeniedEvent extends AbstractDeniedEvent <String>
{
  public DefaultDeniedEvent (final String reason)
  {
    super (reason);
  }

  @RequiredForNetworkSerialization
  private DefaultDeniedEvent()
  {
  }
}
