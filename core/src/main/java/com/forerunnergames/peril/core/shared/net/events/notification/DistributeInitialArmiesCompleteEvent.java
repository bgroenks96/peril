package com.forerunnergames.peril.core.shared.net.events.notification;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.interfaces.GameNotificationEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DistributeInitialArmiesCompleteEvent implements GameNotificationEvent
{
  private final ImmutableSet <Player> players;

  public DistributeInitialArmiesCompleteEvent (final ImmutableSet <Player> players)
  {
    this.players = players;
  }

  public ImmutableSet <Player> getPlayers ()
  {
    return players;
  }

  @RequiredForNetworkSerialization
  private DistributeInitialArmiesCompleteEvent ()
  {
    players = null;
  }
}
