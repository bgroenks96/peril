package com.forerunnergames.peril.common.net;

import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.server.ServerConfiguration;

public final class DefaultGameServerConfiguration implements GameServerConfiguration
{
  private final String gameServerName;
  private final GameServerType gameServerType;
  private final GameConfiguration gameConfig;
  private final ServerConfiguration serverConfig;

  public DefaultGameServerConfiguration (final String gameServerName,
                                         final GameServerType gameServerType,
                                         final GameConfiguration gameConfig,
                                         final ServerConfiguration serverConfig)
  {
    Arguments.checkIsNotNull (gameServerName, "gameServerName");
    Arguments.checkIsNotNull (gameServerType, "gameServerType");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (serverConfig, "serverConfig");

    this.gameServerName = gameServerName;
    this.gameServerType = gameServerType;
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
  public InitialCountryAssignment getInitialCountryAssignment ()
  {
    return gameConfig.getInitialCountryAssignment ();
  }

  @Override
  public String getMapName ()
  {
    return gameConfig.getMapName ();
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return gameConfig.getMapMetadata ();
  }

  @Override
  public MapType getMapType ()
  {
    return gameConfig.getMapType ();
  }

  @Override
  public String getServerAddress ()
  {
    return serverConfig.getServerAddress ();
  }

  @Override
  public int getServerTcpPort ()
  {
    return serverConfig.getServerTcpPort ();
  }

  @Override
  public String getGameServerName ()
  {
    return gameServerName;
  }

  @Override
  public GameServerType getGameServerType ()
  {
    return gameServerType;
  }

  @Override
  public String toString ()
  {
    return String.format (
                          "%1$s: Game Server Name: %2$s | Game Server Type: %3$s | Game Configuration: %4$s"
                                  + " | Server Configuration: %5$s",
                          getClass ().getSimpleName (), gameServerName, gameServerType, gameConfig, serverConfig);
  }

  @RequiredForNetworkSerialization
  private DefaultGameServerConfiguration ()
  {
    gameServerName = null;
    gameServerType = null;
    gameConfig = null;
    serverConfig = null;
  }
}
