package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.collect.ImmutableSet;

public final class PlayGameEvent implements LocalEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;
  private final ImmutableSet <PlayerPacket> playersInGame;
  private final PlayMapActor playMapActor;

  public PlayGameEvent (final GameServerConfiguration gameServerConfig,
                        final ClientConfiguration clientConfig,
                        final ImmutableSet <PlayerPacket> playersInGame,
                        final PlayMapActor playMapActor)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (playersInGame, "playersInGame");
    Arguments.checkHasNoNullElements (playersInGame, "playersInGame");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
    this.playersInGame = playersInGame;
    this.playMapActor = playMapActor;
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

  public MapMetadata getMapMetadata ()
  {
    return gameServerConfig.getMapMetadata ();
  }

  public PlayMapActor getPlayMapActor ()
  {
    return playMapActor;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Game Server Configuration: {} | Client Configuration: {} | Players In Game: {} | Play Map Actor: {}",
                           getClass ().getSimpleName (), gameServerConfig, clientConfig, playersInGame, playMapActor);
  }
}
