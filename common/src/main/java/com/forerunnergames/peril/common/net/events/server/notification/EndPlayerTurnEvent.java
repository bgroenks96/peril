package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;

public class EndPlayerTurnEvent implements PlayerNotificationEvent
{
  private final PlayerPacket player;

  public EndPlayerTurnEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }
}
