package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

public interface PlayerTurnOrderEvent extends PlayerEvent
{
  public PlayerTurnOrder getCurrentTurnOrder ();

  public PlayerTurnOrder getPreviousTurnOrder ();
}
