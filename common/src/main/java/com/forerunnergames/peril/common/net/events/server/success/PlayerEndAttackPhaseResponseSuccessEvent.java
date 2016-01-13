package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerEndAttackPhaseResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent
{
  public PlayerEndAttackPhaseResponseSuccessEvent (final PlayerPacket player)
  {
    super (player);
  }

  @RequiredForNetworkSerialization
  private PlayerEndAttackPhaseResponseSuccessEvent ()
  {
  }
}
