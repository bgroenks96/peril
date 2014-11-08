package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinServerEvent;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultJoinServerEvent implements JoinServerEvent
{
  private final String serverAddress;
  private final int serverTcpPort;

  public DefaultJoinServerEvent (final String serverAddress, final int serverTcpPort)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");

    this.serverAddress = serverAddress;
    this.serverTcpPort = serverTcpPort;
  }

  public String getServerAddress()
  {
    return serverAddress;
  }

  public int getServerTcpPort()
  {
    return serverTcpPort;
  }

  @Override
  public String toString()
  {
    return String.format ("Server address: %1$s | Server port: %2$s (TCP)", serverAddress, serverTcpPort);
  }

  // Required for network serialization
  private DefaultJoinServerEvent()
  {
    serverAddress = null;
    serverTcpPort = 0;
  }
}
