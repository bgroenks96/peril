package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

public final class CreateGameSuccessEvent implements LocalEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;

  public CreateGameSuccessEvent (final GameServerConfiguration gameServerConfig, final ClientConfiguration clientConfig)
  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Game Server Configuration: {} | Client Configuration: {}", getClass ().getSimpleName (),
                           gameServerConfig, clientConfig);
  }
}
