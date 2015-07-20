package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableSet;

public final class DeterminePlayerTurnOrderCompleteEvent implements ServerNotificationEvent
{
  private final ImmutableSet <PlayerPacket> turnOrderedPlayers;

  public DeterminePlayerTurnOrderCompleteEvent (final ImmutableSet <PlayerPacket> turnOrderedPlayers)
  {
    Arguments.checkIsNotNull (turnOrderedPlayers, "turnOrderedPlayers");
    Arguments.checkHasNoNullElements (turnOrderedPlayers, "turnOrderedPlayers");

    this.turnOrderedPlayers = turnOrderedPlayers;
  }

  public ImmutableSet <PlayerPacket> getOrderedPlayers ()
  {
    return turnOrderedPlayers;
  }

  @RequiredForNetworkSerialization
  private DeterminePlayerTurnOrderCompleteEvent ()
  {
    turnOrderedPlayers = null;
  }
}
