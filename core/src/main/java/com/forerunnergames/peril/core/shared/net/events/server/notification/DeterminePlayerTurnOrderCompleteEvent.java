package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.GameNotificationEvent;
import com.forerunnergames.peril.core.shared.net.packets.GamePackets;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DeterminePlayerTurnOrderCompleteEvent implements GameNotificationEvent
{
  private final ImmutableSet <PlayerPacket> turnOrderedPlayers;

  public DeterminePlayerTurnOrderCompleteEvent (final ImmutableSet <Player> orderedPlayers)
  {
    Arguments.checkIsNotNull (orderedPlayers, "orderedPlayers");
    Arguments.checkHasNoNullElements (orderedPlayers, "orderedPlayers");

    this.turnOrderedPlayers = GamePackets.fromPlayers (orderedPlayers);
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
