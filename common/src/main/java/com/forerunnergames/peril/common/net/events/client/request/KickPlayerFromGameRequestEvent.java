package com.forerunnergames.peril.common.net.events.client.request;

import com.forerunnergames.peril.common.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.common.net.events.interfaces.KickEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class KickPlayerFromGameRequestEvent implements KickEvent, ClientRequestEvent
{
  private final KickEvent kickEvent;

  public KickPlayerFromGameRequestEvent (final String reasonForKick)
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
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), kickEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameRequestEvent ()
  {
    kickEvent = null;
  }
}
