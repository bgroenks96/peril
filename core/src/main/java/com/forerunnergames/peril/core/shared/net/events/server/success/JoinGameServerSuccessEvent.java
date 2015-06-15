package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.ClientConfiguration;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

import com.google.common.collect.ImmutableSet;

public final class JoinGameServerSuccessEvent implements SuccessEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;
  private final ImmutableSet <PlayerPacket> playersInGame;

  public JoinGameServerSuccessEvent (final GameServerConfiguration gameServerConfig,
                                     final ClientConfiguration clientConfig,
                                     final ImmutableSet <PlayerPacket> playersInGame)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
    this.playersInGame = playersInGame;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  public ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return playersInGame;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Server Configuration: %2$s | Client Configuration: %3$s | Players In Game: %4$s",
                          getClass ().getSimpleName (), gameServerConfig, clientConfig,
                          Strings.toString (playersInGame));
  }

  @RequiredForNetworkSerialization
  private JoinGameServerSuccessEvent ()
  {
    gameServerConfig = null;
    clientConfig = null;
    playersInGame = null;
  }
}
