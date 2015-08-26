package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

public interface JoinGameServerListener
{
  void onJoinStart (final String playerName, final ServerConfiguration configuration);

  void onConnectToServerSuccess (final ServerConfiguration configuration);

  void onJoinGameServerSuccess (final GameServerConfiguration gameServerConfiguration,
                                final ClientConfiguration clientConfiguration,
                                final ImmutableSet <PlayerPacket> playersInGame);

  void onPlayerJoinGameSuccess (final PlayerPacket player);

  void onConnectToServerFailure (final ServerConfiguration configuration, final String reason);

  void onJoinGameServerFailure (final ClientConfiguration configuration, final String reason);

  void onPlayerJoinGameFailure (final String playerName, final PlayerJoinGameDeniedEvent.Reason reason);

  void onJoinFinish (final GameServerConfiguration gameServerConfiguration,
                     final ClientConfiguration clientConfiguration,
                     final ImmutableSet <PlayerPacket> playersInGame);
}
