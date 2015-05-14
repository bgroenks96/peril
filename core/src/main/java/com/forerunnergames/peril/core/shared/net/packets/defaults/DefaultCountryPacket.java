package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCountryPacket implements CountryPacket
{
  private final String name;
  private final int armyCount;

  public DefaultCountryPacket (final String name, final int armyCount)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
    this.armyCount = armyCount;
  }

  @Override
  public String getCountryName ()
  {
    return name;
  }

  @Override
  public int getArmyCount ()
  {
    return armyCount;
  }

  @Override
  public boolean hasAnyArmies ()
  {
    return armyCount > 0;
  }

  @Override
  public boolean hasAtLeastNArmies (final int n)
  {
    Arguments.checkIsNotNegative (n, "n");

    return armyCount >= n;
  }

  @RequiredForNetworkSerialization
  private DefaultCountryPacket ()
  {
    this.name = null;
    this.armyCount = 0;
  }
}
