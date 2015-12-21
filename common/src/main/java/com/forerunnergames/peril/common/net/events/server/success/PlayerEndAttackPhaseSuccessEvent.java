package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerEndAttackPhaseSuccessEvent implements PlayerSuccessEvent
{
  private final PlayerPacket player;

  public PlayerEndAttackPhaseSuccessEvent (final PlayerPacket player)
  {
    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @RequiredForNetworkSerialization
  private PlayerEndAttackPhaseSuccessEvent ()
  {
    player = null;
  }
}
