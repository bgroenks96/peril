package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultJoinServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinServerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

import com.google.common.collect.ImmutableSet;

public final class JoinMultiplayerServerSuccessEvent implements JoinServerEvent, SuccessEvent
{
  private final String serverName;
  private final JoinServerEvent joinServerEvent;
  private final ImmutableSet <Player> playersInGame;
  private final int playerLimit;

  public JoinMultiplayerServerSuccessEvent (final String serverName,
                                            final String serverAddress,
                                            final int serverTcpPort,
                                            final ImmutableSet <Player> playersInGame,
                                            final int playerLimit)
  {
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNull (serverAddress, "serverAddress");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkIsNotNegative (playerLimit, "playerLimit");

    this.serverName = serverName;
    this.playerLimit = playerLimit;
    this.playersInGame = playersInGame;
    joinServerEvent = new DefaultJoinServerEvent (serverAddress, serverTcpPort);
  }

  public String getServerName()
  {
    return serverName;
  }

  @Override
  public String getServerAddress()
  {
    return joinServerEvent.getServerAddress();
  }

  @Override
  public int getServerTcpPort()
  {
    return joinServerEvent.getServerTcpPort();
  }

  public ImmutableSet <Player> getPlayersInGame()
  {
    return playersInGame;
  }

  public int getPlayerLimit()
  {
    return playerLimit;
  }

  public int getAdditionalPlayersAllowed()
  {
    return playerLimit - playersInGame.size();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: Server name: %2$s | %3$s | Players in game: %4$s | Player limit: %5$s",
            getClass().getSimpleName(), serverName, joinServerEvent, Strings.toString (playersInGame), playerLimit);
  }

  @RequiredForNetworkSerialization
  private JoinMultiplayerServerSuccessEvent()
  {
    serverName = null;
    joinServerEvent = null;
    playersInGame = null;
    playerLimit = 0;
  }
}
