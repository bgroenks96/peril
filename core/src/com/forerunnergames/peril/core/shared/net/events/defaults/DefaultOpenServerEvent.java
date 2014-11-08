package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.OpenServerEvent;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultOpenServerEvent implements OpenServerEvent
{
  private final String serverName;
  private final int serverTcpPort;

  public DefaultOpenServerEvent (final String serverName, final int serverTcpPort)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");

    this.serverName = serverName;
    this.serverTcpPort = serverTcpPort;
  }

  @Override
  public final String getServerName()
  {
    return serverName;
  }

  public final int getServerTcpPort()
  {
    return serverTcpPort;
  }

  @Override
  public String toString()
  {
    return String.format ("Server name: %1$s | Server port: %2$s (TCP)", serverName, serverTcpPort);
  }

  // Required for network serialization
  private DefaultOpenServerEvent()
  {
    serverName = null;
    serverTcpPort = 0;
  }
}
