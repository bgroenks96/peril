package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractArmiesChangedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.annotations.AllowNegative;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class CountryArmiesChangedEvent extends AbstractArmiesChangedEvent
{
  private final String countryName;

  public CountryArmiesChangedEvent (final String countryName, @AllowNegative final int deltaArmyCount)
  {
    super (deltaArmyCount);

    Arguments.checkIsNotNull (countryName, "countryName");

    this.countryName = countryName;
  }

  public String getCountryName ()
  {
    return countryName;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Country Name: {}", super.toString (), countryName);
  }

  @RequiredForNetworkSerialization
  private CountryArmiesChangedEvent ()
  {
    super (0);
    countryName = null;
  }
}
