package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public abstract class AbstractCountryArmiesChangedEvent extends AbstractCountryEvent
        implements CountryArmiesChangedEvent, ServerNotificationEvent
{
  private final int deltaArmyCount;

  /**
   * @param deltaArmyCount
   *          army change delta value; negative values are ALLOWED
   */
  protected AbstractCountryArmiesChangedEvent (final CountryPacket country, @AllowNegative final int deltaArmyCount)
  {
    super (country);

    this.deltaArmyCount = deltaArmyCount;
  }

  @Override
  public int getCountryDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeltaArmyCount: {}", super.toString (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  protected AbstractCountryArmiesChangedEvent ()
  {
    deltaArmyCount = 0;
  }
}
