package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCountryArmiesChangedEvent extends AbstractCountryArmiesChangedEvent
{
  public DefaultCountryArmiesChangedEvent (final CountryPacket country, final int deltaArmyCount)
  {
    super (country, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultCountryArmiesChangedEvent ()
  {
  }
}
