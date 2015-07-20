package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.SuccessEvent;

public final class KickPlayerFromGameSuccessEvent implements KickEvent, ServerEvent, SuccessEvent
{
  private final KickEvent kickEvent;

  public KickPlayerFromGameSuccessEvent (final String reasonForKick)
  {
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");

    kickEvent = new DefaultKickEvent (reasonForKick);
  }

  @Override
  public String getReasonForKick ()
  {
    return kickEvent.getReasonForKick ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: | %2$s", getClass ().getSimpleName (), kickEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameSuccessEvent ()
  {
    kickEvent = null;
  }
}
