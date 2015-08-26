package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

public final class CreateGameEvent implements Event
{
  private final String serverName;
  private final GameConfiguration gameConfig;
  private final String playerName;

  public CreateGameEvent (final String serverName, final GameConfiguration gameConfig, final String playerName)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (playerName, "playerName");

    this.serverName = serverName;
    this.gameConfig = gameConfig;
    this.playerName = playerName;
  }

  public String getServerName ()
  {
    return serverName;
  }

  public GameConfiguration getGameConfiguration ()
  {
    return gameConfig;
  }

  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Server Name: {} | Game Configuration: {} | Player Name: {}",
                           getClass ().getSimpleName (), serverName, gameConfig, playerName);
  }
}
