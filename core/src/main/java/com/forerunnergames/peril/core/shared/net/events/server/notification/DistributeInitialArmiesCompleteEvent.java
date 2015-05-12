package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DistributeInitialArmiesCompleteEvent implements GameNotificationEvent
{
  private final ImmutableSet <PlayerPacket> players;

  public DistributeInitialArmiesCompleteEvent (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    this.players = players;
  }

  public ImmutableSet <PlayerPacket> getPlayers ()
  {
    return players;
  }

  @RequiredForNetworkSerialization
  private DistributeInitialArmiesCompleteEvent ()
  {
    players = null;
  }
}
