package com.forerunnergames.peril.core.shared.net.events.notification;

import com.forerunnergames.peril.core.shared.net.events.interfaces.GameNotificationEvent;
import com.forerunnergames.tools.common.Arguments;

public final class CountryArmiesChangedEvent implements GameNotificationEvent
{
  private final String countryName;
  private final int oldArmyCount;
  private final int newArmyCount;

  public CountryArmiesChangedEvent (final String countryName, final int oldArmyCount, final int newArmyCount)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    this.countryName = countryName;
    this.oldArmyCount = oldArmyCount;
    this.newArmyCount = newArmyCount;
  }

  public String getCountryName()
  {
    return countryName;
  }

  public int getOldArmyCount()
  {
    return oldArmyCount;
  }

  public int getNewArmyCount()
  {
    return newArmyCount;
  }
}
