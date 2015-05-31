package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.territory.AbstractTerritoryPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCountryPacket extends AbstractTerritoryPacket implements CountryPacket
{
  private final int armyCount;

  public DefaultCountryPacket (final int countryId, final String name, final int armyCount)
  {
    super (name, countryId);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    this.armyCount = armyCount;
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
    super (null, 0);

    this.armyCount = 0;
  }
}
