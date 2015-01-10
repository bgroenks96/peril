package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class DefaultKickEvent implements KickEvent
{
  private final String reasonForKick;

  public DefaultKickEvent (final String reasonForKick)
  {
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");

    this.reasonForKick = reasonForKick;
  }

  @Override
  public String getReasonForKick()
  {
    return reasonForKick;
  }

  @Override
  public String toString()
  {
    return String.format ("Reason for kick: %1$s", reasonForKick);
  }

  @RequiredForNetworkSerialization
  private DefaultKickEvent()
  {
    reasonForKick = null;
  }
}
