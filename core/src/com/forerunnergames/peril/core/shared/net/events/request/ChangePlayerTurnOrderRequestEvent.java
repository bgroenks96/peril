package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerTurnOrderEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerTurnOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class ChangePlayerTurnOrderRequestEvent implements PlayerTurnOrderEvent, RequestEvent
{
  private final PlayerTurnOrderEvent playerTurnOrderEvent;

  public ChangePlayerTurnOrderRequestEvent (final Player player,
                                            final PlayerTurnOrder currentTurnOrder,
                                            final PlayerTurnOrder previousTurnOrder)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentTurnOrder, "currentTurnOrder");
    Arguments.checkIsNotNull (previousTurnOrder, "previousTurnOrder");

    playerTurnOrderEvent = new DefaultPlayerTurnOrderEvent (player, currentTurnOrder, previousTurnOrder);
  }

  @Override
  public Player getPlayer()
  {
    return playerTurnOrderEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerTurnOrderEvent.getPlayerName();
  }

  @Override
  public PlayerTurnOrder getCurrentTurnOrder()
  {
    return playerTurnOrderEvent.getCurrentTurnOrder();
  }

  @Override
  public PlayerTurnOrder getPreviousTurnOrder()
  {
    return playerTurnOrderEvent.getPreviousTurnOrder();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), playerTurnOrderEvent);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerTurnOrderRequestEvent()
  {
    playerTurnOrderEvent = null;
  }
}
