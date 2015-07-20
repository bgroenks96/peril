package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableSet;

public final class DistributeInitialArmiesCompleteEvent implements ServerNotificationEvent
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
