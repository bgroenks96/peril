package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.collect.ImmutableSet;

public final class JoinGameSuccessEvent implements LocalEvent
{
  private final ImmutableSet <PlayerPacket> players;
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;

  public JoinGameSuccessEvent (final ImmutableSet <PlayerPacket> players,
                               final GameServerConfiguration gameServerConfig,
                               final ClientConfiguration clientConfig)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");

    this.players = players;
    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
  }

  public MapMetadata getMapMetadata ()
  {
    return gameServerConfig.getMapMetadata ();
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  public ImmutableSet <PlayerPacket> getPlayers ()
  {
    return players;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Players: {} | Game Server Configuration {} | Client Configuration: {}",
                           getClass ().getSimpleName (), players, gameServerConfig, clientConfig);
  }
}
