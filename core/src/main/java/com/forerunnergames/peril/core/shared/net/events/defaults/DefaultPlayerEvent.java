package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerEvent implements PlayerEvent
{
  private final Player player;

  public DefaultPlayerEvent (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public Player getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public String toString ()
  {
    return String.format ("Player: %1$s", player);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerEvent ()
  {
    player = null;
  }
}
