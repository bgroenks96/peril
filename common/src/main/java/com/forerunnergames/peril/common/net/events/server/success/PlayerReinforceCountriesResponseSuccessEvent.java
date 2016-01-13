package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceCountriesResponseSuccessEvent extends AbstractPlayerArmiesChangedEvent
        implements PlayerResponseSuccessEvent, PlayerArmiesChangedEvent
{
  public PlayerReinforceCountriesResponseSuccessEvent (final PlayerPacket player, final int deltaArmyCount)
  {
    super (player, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseSuccessEvent ()
  {
  }
}
