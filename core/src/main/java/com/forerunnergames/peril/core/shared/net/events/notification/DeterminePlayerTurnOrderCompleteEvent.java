package com.forerunnergames.peril.core.shared.net.events.notification;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.interfaces.GameNotificationEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DeterminePlayerTurnOrderCompleteEvent implements GameNotificationEvent
{
  private final ImmutableSet <Player> orderedPlayers;

  public DeterminePlayerTurnOrderCompleteEvent (final ImmutableSet <Player> orderedPlayers)
  {
    Arguments.checkIsNotNull (orderedPlayers, "orderedPlayers");
    Arguments.checkHasNoNullElements (orderedPlayers, "orderedPlayers");

    this.orderedPlayers = orderedPlayers;
  }

  public ImmutableSet <Player> getOrderedPlayers ()
  {
    return orderedPlayers;
  }

  @RequiredForNetworkSerialization
  private DeterminePlayerTurnOrderCompleteEvent ()
  {
    orderedPlayers = null;
  }
}
