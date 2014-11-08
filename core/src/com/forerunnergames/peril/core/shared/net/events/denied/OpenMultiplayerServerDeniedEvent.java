package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.OpenServerEvent;
import com.forerunnergames.peril.core.shared.net.events.request.OpenMultiplayerServerRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class OpenMultiplayerServerDeniedEvent implements DeniedEvent, OpenServerEvent
{
  private final OpenMultiplayerServerRequestEvent requestEvent;
  private final DeniedEvent deniedEvent;

  public OpenMultiplayerServerDeniedEvent (final OpenMultiplayerServerRequestEvent event, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    requestEvent = event;
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  @Override
  public String getServerName()
  {
    return requestEvent.getServerName();
  }

  @Override
  public int getServerTcpPort()
  {
    return requestEvent.getServerTcpPort();
  }

  @Override
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Original request: %2$s | %3$s", getClass().getSimpleName(), requestEvent, deniedEvent);
  }

  // Required for network serialization
  private OpenMultiplayerServerDeniedEvent()
  {
    requestEvent = null;
    deniedEvent = null;
  }
}
