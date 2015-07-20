package com.forerunnergames.peril.core.shared.net.events.server.notification;

import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class PlayerLeaveGameEvent implements ServerNotificationEvent
{
  private final PlayerPacket player;

  public PlayerLeaveGameEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Player: %2$s", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameEvent ()
  {
    player = null;
  }
}
