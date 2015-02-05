package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerTurnOrderEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerTurnOrderEvent implements PlayerTurnOrderEvent
{
  private final PlayerTurnOrder requestedTurnOrder;

  public DefaultPlayerTurnOrderEvent (final PlayerTurnOrder requestedTurnOrder)
  {
    Arguments.checkIsNotNull (requestedTurnOrder, "requestedTurnOrder");

    this.requestedTurnOrder = requestedTurnOrder;
  }

  @Override
  public PlayerTurnOrder getRequestedTurnOrder ()
  {
    return requestedTurnOrder;
  }

  @Override
  public String toString ()
  {
    return String.format ("Requested turn order: %1$s", requestedTurnOrder);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerTurnOrderEvent ()
  {
    requestedTurnOrder = null;
  }
}
