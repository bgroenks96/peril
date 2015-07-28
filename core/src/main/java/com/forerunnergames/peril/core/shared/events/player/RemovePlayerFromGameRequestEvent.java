package com.forerunnergames.peril.core.shared.events.player;

import com.forerunnergames.peril.core.shared.events.AbstractInternalCommunicationEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;

public class RemovePlayerFromGameRequestEvent extends AbstractInternalCommunicationEvent
{
  private final PlayerPacket player;

  public RemovePlayerFromGameRequestEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }
}
