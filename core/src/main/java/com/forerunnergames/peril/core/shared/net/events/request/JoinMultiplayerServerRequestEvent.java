package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultJoinServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class JoinMultiplayerServerRequestEvent implements JoinServerEvent, RequestEvent
{
  private final JoinServerEvent joinServerEvent;

  public JoinMultiplayerServerRequestEvent (final String serverAddress, final int serverTcpPort)
  {
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");

    joinServerEvent = new DefaultJoinServerEvent (serverAddress, serverTcpPort);
  }

  @Override
  public String getServerAddress ()
  {
    return joinServerEvent.getServerAddress ();
  }

  @Override
  public int getServerTcpPort ()
  {
    return joinServerEvent.getServerTcpPort ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), joinServerEvent);
  }

  @RequiredForNetworkSerialization
  private JoinMultiplayerServerRequestEvent ()
  {
    joinServerEvent = null;
  }
}
