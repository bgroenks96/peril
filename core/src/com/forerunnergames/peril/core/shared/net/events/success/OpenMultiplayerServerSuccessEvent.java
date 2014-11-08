package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultOpenServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.OpenServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class OpenMultiplayerServerSuccessEvent implements OpenServerEvent, SuccessEvent
{
  private final OpenServerEvent openServerEvent;

  public OpenMultiplayerServerSuccessEvent (final String serverName, final int serverTcpPort)
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
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), openServerEvent.toString());
  }

  // Required for network serialization
  private OpenMultiplayerServerSuccessEvent()
  {
    openServerEvent = null;
  }
}
