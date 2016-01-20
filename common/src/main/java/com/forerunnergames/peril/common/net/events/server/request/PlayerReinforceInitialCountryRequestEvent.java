package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceInitialCountryRequestEvent extends AbstractPlayerEvent
        implements PlayerInputRequestEvent
{
  public PlayerReinforceInitialCountryRequestEvent (final PlayerPacket player)
  {
    super (player);
  }

  public int getTotalReinforcements ()
  {
    return getPlayer ().getArmiesInHand ();
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceInitialCountryRequestEvent ()
  {
  }
}
