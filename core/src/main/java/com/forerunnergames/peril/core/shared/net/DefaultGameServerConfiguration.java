package com.forerunnergames.peril.core.shared.net;

import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.model.rules.GameMode;
import com.forerunnergames.peril.core.model.rules.InitialCountryAssignment;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.ServerConfiguration;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class DefaultGameServerConfiguration implements GameServerConfiguration
{
  private final GameConfiguration gameConfig;
  private final ServerConfiguration serverConfig;

  public DefaultGameServerConfiguration (final GameConfiguration gameConfig, final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    this.gameConfig = gameConfig;
    this.serverConfig = serverConfig;
  }

  @Override
  public GameMode getGameMode ()
  {
    return gameConfig.getGameMode ();
  }

  @Override
  public int getPlayerLimit ()
  {
    return gameConfig.getPlayerLimit ();
  }

  @Override
  public int getWinPercentage ()
  {
    return gameConfig.getWinPercentage ();
  }

  @Override
  public int getTotalCountryCount ()
  {
    return gameConfig.getTotalCountryCount ();
  }

  @Override
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return gameConfig.getInitialCountryAssignment ();
  }

  @Override
  public String getServerAddress ()
  {
    return serverConfig.getServerAddress ();
  }

  @Override
  public String getServerName ()
  {
    return serverConfig.getServerName ();
  }

  @Override
  public int getServerTcpPort ()
  {
    return serverConfig.getServerTcpPort ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Configuration: %2$s | Server Configuration: %3$s", ((Object) this).getClass ()
                    .getSimpleName (), gameConfig, serverConfig);
  }

  @RequiredForNetworkSerialization
  private DefaultGameServerConfiguration ()
  {
    gameConfig = null;
    serverConfig = null;
  }
}
