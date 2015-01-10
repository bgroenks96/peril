package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultOpenServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.OpenServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class OpenMultiplayerServerRequestEvent implements OpenServerEvent, RequestEvent
{
  private final OpenServerEvent openServerEvent;

  public OpenMultiplayerServerRequestEvent (final String serverName, final int serverTcpPort)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");

    openServerEvent = new DefaultOpenServerEvent (serverName, serverTcpPort);
  }

  @Override
  public String getServerName()
  {
    return openServerEvent.getServerName();
  }

  @Override
  public int getServerTcpPort()
  {
    return openServerEvent.getServerTcpPort();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), openServerEvent);
  }

  @RequiredForNetworkSerialization
  private OpenMultiplayerServerRequestEvent()
  {
    openServerEvent = null;
  }
}
