package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class JoinGameServerRequestEvent implements ClientRequestEvent
{
  private final ServerConfiguration config;

  public JoinGameServerRequestEvent (final ServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    this.config = config;
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return config;
  }

  public String getServerAddress ()
  {
    return config.getServerAddress ();
  }

  public int getServerTcpPort ()
  {
    return config.getServerTcpPort ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Server Configuration: %2$s", getClass ().getSimpleName (), config);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerRequestEvent ()
  {
    config = null;
  }
}
