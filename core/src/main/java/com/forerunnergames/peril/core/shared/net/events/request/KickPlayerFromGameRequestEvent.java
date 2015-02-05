package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

public final class KickPlayerFromGameRequestEvent implements KickEvent, RequestEvent
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
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), kickEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameRequestEvent ()
  {
    kickEvent = null;
  }
}
