package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class CreateGameServerRequestEvent implements CreateGameServerEvent, ClientRequestEvent
{
  private final GameServerConfiguration config;

  public CreateGameServerRequestEvent (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    this.config = config;
  }

  @Override
  public GameServerConfiguration getGameServerConfiguration ()
  {
    return config;
  }

  public String getServerAddress ()
  {
    return config.getServerAddress ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Server Configuration: %2$s", getClass ().getSimpleName (), config);
  }

  @RequiredForNetworkSerialization
  private CreateGameServerRequestEvent ()
  {
    config = null;
  }
}
