package com.forerunnergames.peril.core.shared.net.events.notification;

import com.forerunnergames.peril.core.shared.net.events.interfaces.GameNotificationEvent;
import com.forerunnergames.tools.common.Arguments;

public final class CountryArmiesChangedEvent implements GameNotificationEvent
{
  private final String countryName;
  private final int deltaArmyCount;

  public CountryArmiesChangedEvent (final String countryName, final int deltaArmyCount)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    this.countryName = countryName;
    this.deltaArmyCount = deltaArmyCount;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }
}
