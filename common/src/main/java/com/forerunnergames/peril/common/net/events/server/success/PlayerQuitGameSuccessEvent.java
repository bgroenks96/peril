package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerQuitGameSuccessEvent extends AbstractPlayerEvent implements PlayerSuccessEvent
{
  private final ImmutableSet <PlayerPacket> remainingPlayersInGame;
  private final ImmutableSet <PlayerPacket> disconnectedPlayers;

  public PlayerQuitGameSuccessEvent (final PlayerPacket player,
                                     final ImmutableSet <PlayerPacket> remainingPlayersInGame,
                                     final ImmutableSet <PlayerPacket> disconnectedPlayers)
  {
    super (player);

    this.remainingPlayersInGame = remainingPlayersInGame;
    this.disconnectedPlayers = disconnectedPlayers;
  }

  public ImmutableSet <PlayerPacket> getRemainingPlayersInGame ()
  {
    return remainingPlayersInGame;
  }

  public ImmutableSet <PlayerPacket> getDisconnectedPlayers ()
  {
    return disconnectedPlayers;
  }

  @RequiredForNetworkSerialization
  private PlayerQuitGameSuccessEvent ()
  {
    remainingPlayersInGame = null;
    disconnectedPlayers = null;
  }
}
