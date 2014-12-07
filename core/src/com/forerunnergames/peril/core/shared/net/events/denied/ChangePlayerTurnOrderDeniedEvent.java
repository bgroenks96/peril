package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerTurnOrderEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerTurnOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class ChangePlayerTurnOrderDeniedEvent implements PlayerTurnOrderEvent, DeniedEvent <String>
{
  private final PlayerTurnOrderEvent playerTurnOrderEvent;
  private final DeniedEvent <String> deniedEvent;

  public ChangePlayerTurnOrderDeniedEvent (final Player player,
                                           final PlayerTurnOrder currentTurnOrder,
                                           final PlayerTurnOrder previousTurnOrder,
                                           final String reason)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentTurnOrder, "currentTurnOrder");
    Arguments.checkIsNotNull (previousTurnOrder, "previousTurnOrder");
    Arguments.checkIsNotNull (reason, "reason");

    playerTurnOrderEvent = new DefaultPlayerTurnOrderEvent (player, currentTurnOrder, previousTurnOrder);
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason()
  {
    return deniedEvent.getReason();
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
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), playerTurnOrderEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerTurnOrderDeniedEvent()
  {
    playerTurnOrderEvent = null;
    deniedEvent = null;
  }
}
