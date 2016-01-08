package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class BeginReinforcementPhaseEvent implements PlayerNotificationEvent
{
  private final PlayerPacket currentPlayer;

  public BeginReinforcementPhaseEvent (final PlayerPacket currentPlayer)
  {
    Arguments.checkIsNotNull (currentPlayer, "currentPlayer");

    this.currentPlayer = currentPlayer;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return currentPlayer;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Current Player: {}", getClass ().getSimpleName (), currentPlayer);
  }

  @RequiredForNetworkSerialization
  private BeginReinforcementPhaseEvent ()
  {
    currentPlayer = null;
  }
}
