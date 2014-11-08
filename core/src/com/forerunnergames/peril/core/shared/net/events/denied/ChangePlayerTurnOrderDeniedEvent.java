package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.model.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerTurnOrderEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerTurnOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class ChangePlayerTurnOrderDeniedEvent implements PlayerTurnOrderEvent, DeniedEvent
{
  private final PlayerTurnOrderEvent playerTurnOrderEvent;
  private final DeniedEvent deniedEvent;

  public ChangePlayerTurnOrderDeniedEvent (final Player player,
                                           final PlayerTurnOrder currentTurnOrder,
                                           final PlayerTurnOrder previousTurnOrder,
                                           final String reasonForDenial)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentTurnOrder, "currentTurnOrder");
    Arguments.checkIsNotNull (previousTurnOrder, "previousTurnOrder");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    playerTurnOrderEvent = new DefaultPlayerTurnOrderEvent (player, currentTurnOrder, previousTurnOrder);
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
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
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s",
            getClass().getSimpleName(), playerTurnOrderEvent.toString(), deniedEvent.toString());
  }

  // Required for network serialization
  private ChangePlayerTurnOrderDeniedEvent()
  {
    playerTurnOrderEvent = null;
    deniedEvent = null;
  }
}
