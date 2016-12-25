package com.forerunnergames.peril.client.events;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.events.local.LocalEvent;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.util.UUID;

public final class ReconnectToServerEvent implements LocalEvent
{
  private final String playerName;
  private final UUID playerSecretId;
  private final ServerConfiguration serverConfiguration;

  public ReconnectToServerEvent (final String playerName,
                                 final UUID playerSecretId,
                                 final ServerConfiguration serverConfiguration)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (playerSecretId, "playerSecretId");
    Arguments.checkIsNotNull (serverConfiguration, "serverConfiguration");

    this.playerName = playerName;
    this.playerSecretId = playerSecretId;
    this.serverConfiguration = serverConfiguration;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  public UUID getPlayerSecretId ()
  {
    return playerSecretId;
  }

  public ServerConfiguration getServerConfiguration ()
  {
    return serverConfiguration;
  }
}
