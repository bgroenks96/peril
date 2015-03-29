package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.rules.GameConfiguration;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultJoinGameServerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.JoinGameServerEvent;
import com.forerunnergames.peril.core.shared.net.packets.GamePackets;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.ServerConfiguration;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

import com.google.common.collect.ImmutableSet;

public final class JoinGameServerSuccessEvent implements JoinGameServerEvent, SuccessEvent
{
  private final GameConfiguration gameConfig;
  private final JoinGameServerEvent joinGameServerEvent;
  private final ImmutableSet <PlayerPacket> playersInGame;

  public JoinGameServerSuccessEvent (final ServerConfiguration serverConfig,
                                     final GameConfiguration gameConfig,
                                     final ImmutableSet <Player> playersInGame)

  {
    Arguments.checkIsNotNull (serverConfig, "serverConfig");
    Arguments.checkIsNotNull (gameConfig, "gameConfig");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");

    joinGameServerEvent = new DefaultJoinGameServerEvent (serverConfig);
    this.gameConfig = gameConfig;
    this.playersInGame = GamePackets.fromPlayers (playersInGame);
  }

  public ImmutableSet <PlayerPacket> getPlayersInGame ()
  {
    return playersInGame;
  }

  @Override
  public ServerConfiguration getConfiguration ()
  {
    return joinGameServerEvent.getConfiguration ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Game Configuration: %2$s | Players In Game: %3$s | %4$s", ((Object) this).getClass ()
            .getSimpleName (), gameConfig, Strings.toString (playersInGame), joinGameServerEvent);
  }

  @RequiredForNetworkSerialization
  private JoinGameServerSuccessEvent ()
  {
    gameConfig = null;
    joinGameServerEvent = null;
    playersInGame = null;
  }
}
