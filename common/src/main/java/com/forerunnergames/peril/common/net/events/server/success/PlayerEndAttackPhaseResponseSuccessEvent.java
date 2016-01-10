package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerEndAttackPhaseResponseSuccessEvent implements PlayerResponseSuccessEvent
{
  private final PlayerPacket player;

  public PlayerEndAttackPhaseResponseSuccessEvent (final PlayerPacket player)
  {
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
  private PlayerEndAttackPhaseResponseSuccessEvent ()
  {
    player = null;
  }
}
