package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerWinGameEvent implements PlayerNotificationEvent
{
  private final PlayerPacket player;

  public PlayerWinGameEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}]", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerWinGameEvent ()
  {
    player = null;
  }
}
