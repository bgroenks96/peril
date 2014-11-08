package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinServerEvent;
import com.forerunnergames.peril.core.shared.net.events.request.JoinMultiplayerServerRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class JoinMultiplayerServerDeniedEvent implements DeniedEvent, JoinServerEvent
{
  private final JoinMultiplayerServerRequestEvent requestEvent;
  private final DeniedEvent deniedEvent;

  public JoinMultiplayerServerDeniedEvent (final JoinMultiplayerServerRequestEvent event, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    requestEvent = event;
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  @Override
  public String getServerAddress()
  {
    return requestEvent.getServerAddress();
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
  private JoinMultiplayerServerDeniedEvent()
  {
    requestEvent = null;
    deniedEvent = null;
  }
}
