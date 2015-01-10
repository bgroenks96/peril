package com.forerunnergames.peril.core.shared.net.events.interfaces;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;

public interface PlayerColorEvent extends PlayerEvent
{
  public PlayerColor getCurrentColor ();

  public PlayerColor getPreviousColor ();
}
