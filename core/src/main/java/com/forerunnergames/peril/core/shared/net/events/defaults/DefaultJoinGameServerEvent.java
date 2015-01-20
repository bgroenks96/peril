package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.ServerConfiguration;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class DefaultJoinGameServerEvent implements JoinGameServerEvent
{
  private final ServerConfiguration config;

  public DefaultJoinGameServerEvent (final ServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    this.config = config;
  }

  @Override
  public ServerConfiguration getConfiguration ()
  {
    return config;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Server Configuration: %2$s", ((Object) this).getClass ().getSimpleName (), config);
  }

  @RequiredForNetworkSerialization
  private DefaultJoinGameServerEvent ()
  {
    config = null;
  }
}
