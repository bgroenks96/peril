package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class PlayerLeaveGameSuccessEvent implements PlayerEvent, SuccessEvent
{
  private final PlayerEvent playerEvent;

  public PlayerLeaveGameSuccessEvent (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    playerEvent = new DefaultPlayerEvent (player);
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
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), playerEvent);
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameSuccessEvent()
  {
    playerEvent = null;
  }
}