package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableSortedSet;

public final class DeterminePlayerTurnOrderCompleteEvent implements ServerNotificationEvent
{
  private final ImmutableSortedSet <PlayerPacket> turnOrderedPlayers;

  public DeterminePlayerTurnOrderCompleteEvent (final ImmutableSortedSet <PlayerPacket> turnOrderedPlayers)
  {
    Arguments.checkIsNotNull (turnOrderedPlayers, "turnOrderedPlayers");
    Arguments.checkHasNoNullElements (turnOrderedPlayers, "turnOrderedPlayers");

    this.turnOrderedPlayers = turnOrderedPlayers;
  }

  public ImmutableSortedSet <PlayerPacket> getPlayersSortedByTurnOrder ()
  {
    return turnOrderedPlayers;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Turn Ordered Players: {}", getClass ().getSimpleName (), turnOrderedPlayers);
  }

  @RequiredForNetworkSerialization
  private DeterminePlayerTurnOrderCompleteEvent ()
  {
    turnOrderedPlayers = null;
  }
}
