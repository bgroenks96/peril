package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultJoinGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.ServerConfiguration;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

public final class JoinGameServerRequestEvent implements JoinGameServerEvent, RequestEvent
{
  private final JoinGameServerEvent joinGameServerEvent;

  public JoinGameServerRequestEvent (final ServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    joinGameServerEvent = new DefaultJoinGameServerEvent (config);
  }

  @Override
  public ServerConfiguration getConfiguration ()
  {
    return joinGameServerEvent.getConfiguration ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), joinGameServerEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerRequestEvent ()
  {
    joinGameServerEvent = null;
  }
}
