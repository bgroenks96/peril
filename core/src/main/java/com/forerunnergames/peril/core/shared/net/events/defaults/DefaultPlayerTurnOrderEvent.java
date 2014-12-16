package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerTurnOrderEvent;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultPlayerTurnOrderEvent implements PlayerTurnOrderEvent
{
  private final PlayerEvent playerEvent;
  private final PlayerTurnOrder currentTurnOrder;
  private final PlayerTurnOrder previousTurnOrder;

  public DefaultPlayerTurnOrderEvent (final Player player,
                                      final PlayerTurnOrder currentTurnOrder,
                                      final PlayerTurnOrder previousTurnOrder)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentTurnOrder, "currentTurnOrder");
    Arguments.checkIsNotNull (previousTurnOrder, "previousTurnOrder");

    playerEvent = new DefaultPlayerEvent (player);
    this.currentTurnOrder = currentTurnOrder;
    this.previousTurnOrder = previousTurnOrder;
  }

  @Override
  public Player getPlayer()
  {
    return playerEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerEvent.getPlayerName();
  }

  @Override
  public PlayerTurnOrder getCurrentTurnOrder()
  {
    return currentTurnOrder;
  }

  @Override
  public PlayerTurnOrder getPreviousTurnOrder()
  {
    return previousTurnOrder;
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s | Current turn order: %2$s | Previous turn order: %3$s",
            playerEvent, currentTurnOrder, previousTurnOrder);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerTurnOrderEvent()
  {
    playerEvent = null;
    currentTurnOrder = null;
    previousTurnOrder = null;
  }
}
