package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.tools.common.Event;

public interface PlayerEvent extends Event
{
  public Player getPlayer();
  public String getPlayerName();
}
