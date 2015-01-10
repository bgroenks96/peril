package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public abstract class AbstractDeniedEvent <T> implements DeniedEvent <T>
{
  private final T reason;

  protected AbstractDeniedEvent (final T reason)
  {
    Arguments.checkIsNotNull (reason, "reason");

    this.reason = reason;
  }

  @Override
  public final T getReason()
  {
    return reason;
  }

  @Override
  public String toString()
  {
    return String.format ("Reason for denial: %1$s", reason);
  }

  @RequiredForNetworkSerialization
  protected AbstractDeniedEvent()
  {
    reason = null;
  }
}
