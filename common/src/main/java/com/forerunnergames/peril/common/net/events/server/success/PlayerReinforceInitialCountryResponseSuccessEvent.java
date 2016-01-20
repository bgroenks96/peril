package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceInitialCountryResponseSuccessEvent extends AbstractPlayerCountryArmiesChangedEvent
        implements PlayerSuccessEvent
{
  public PlayerReinforceInitialCountryResponseSuccessEvent (final PlayerPacket player,
                                                            final CountryPacket country,
                                                            final int reinforcementCount)
  {
    super (player, country, -reinforcementCount, reinforcementCount);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceInitialCountryResponseSuccessEvent ()
  {
  }
}
