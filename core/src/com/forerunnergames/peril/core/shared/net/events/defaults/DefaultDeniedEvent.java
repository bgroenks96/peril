package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class DefaultDeniedEvent implements DeniedEvent
{
  private final String reasonForDenial;

  public DefaultDeniedEvent (final String reasonForDenial)
  {
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    this.reasonForDenial = reasonForDenial;
  }

  @Override
  public String getReasonForDenial()
  {
    return reasonForDenial;
  }

  @Override
  public String toString()
  {
    return String.format ("Reason for denial: %1$s", reasonForDenial);
  }

  // Required for network serialization
  private DefaultDeniedEvent()
  {
    reasonForDenial = null;
  }
}
