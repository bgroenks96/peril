package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCreateGameServerEvent implements CreateGameServerEvent
{
  private final GameServerConfiguration config;

  public DefaultCreateGameServerEvent (final GameServerConfiguration config)
  {
    Arguments.checkIsNotNull (config, "config");

    this.config = config;
  }

  @Override
  public GameServerConfiguration getConfiguration ()
  {
    return config;
  }

  @Override
  public String toString ()
  {
    return String
      .format ("%1$s: Game Server Configuration: %2$s", ((Object) this).getClass ().getSimpleName (), config);
  }

  @RequiredForNetworkSerialization
  private DefaultCreateGameServerEvent ()
  {
    config = null;
  }
}
