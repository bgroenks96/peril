package com.forerunnergames.peril.core.shared.net.events.server.success;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

public final class PlayerJoinGameSuccessEvent implements SuccessEvent
{
  private final Player player;

  public PlayerJoinGameSuccessEvent (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public Player getPlayer ()
  {
    return player;
  }

  public String getPlayerName ()
  {
    return player.getName ();
  }

  public PlayerColor getPlayerColor ()
  {
    return player.getColor ();
  }

  public PlayerTurnOrder getPlayerTurnOrder ()
  {
    return player.getTurnOrder ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Player: %2$s", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerJoinGameSuccessEvent ()
  {
    player = null;
  }
}
