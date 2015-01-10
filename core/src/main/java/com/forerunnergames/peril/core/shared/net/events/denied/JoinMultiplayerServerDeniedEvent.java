package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinServerEvent;
import com.forerunnergames.peril.core.shared.net.events.request.JoinMultiplayerServerRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class JoinMultiplayerServerDeniedEvent implements DeniedEvent <String>, JoinServerEvent
{
  private final JoinMultiplayerServerRequestEvent requestEvent;
  private final DeniedEvent <String> deniedEvent;

  public JoinMultiplayerServerDeniedEvent (final JoinMultiplayerServerRequestEvent event, final String reason)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsNotNull (reason, "reason");

    requestEvent = event;
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason()
  {
    return deniedEvent.getReason();
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
  public String toString()
  {
    return String.format ("%1$s: Original request: %2$s | %3$s", getClass().getSimpleName(), requestEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private JoinMultiplayerServerDeniedEvent()
  {
    requestEvent = null;
    deniedEvent = null;
  }
}
