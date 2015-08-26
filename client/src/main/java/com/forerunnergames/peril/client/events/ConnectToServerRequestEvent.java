package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class ConnectToServerRequestEvent implements LocalEvent
{
  private final ServerConfiguration serverConfig;

  public ConnectToServerRequestEvent (final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    this.serverConfig = serverConfig;
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return serverConfig;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Server Configuration: {}", getClass ().getSimpleName (), serverConfig);
  }
}
