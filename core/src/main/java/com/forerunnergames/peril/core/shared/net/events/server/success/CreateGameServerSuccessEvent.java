package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCreateGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CreateGameServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.ClientConfiguration;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

public final class CreateGameServerSuccessEvent implements CreateGameServerEvent, SuccessEvent
{
  private final CreateGameServerEvent createGameServerEvent;
  private final ClientConfiguration clientConfig;

  public CreateGameServerSuccessEvent (final GameServerConfiguration gameServerConfig,
                                       final ClientConfiguration clientConfig)
  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");

    createGameServerEvent = new DefaultCreateGameServerEvent (gameServerConfig);

    this.clientConfig = clientConfig;
  }

  @Override
  public GameServerConfiguration getGameServerConfiguration ()
  {
    return createGameServerEvent.getGameServerConfiguration ();
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s | Client Configuration: %3$s", getClass ().getSimpleName (),
                          createGameServerEvent, clientConfig);
  }

  @RequiredForNetworkSerialization
  private CreateGameServerSuccessEvent ()
  {
    createGameServerEvent = null;
    clientConfig = null;
  }
}
