package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.shared.net.packets.territory.AbstractTerritoryPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public final class DefaultCountryPacket extends AbstractTerritoryPacket implements CountryPacket
{
  private final int armyCount;

  public DefaultCountryPacket (final UUID countryId, final String name, final int armyCount)
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

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Army Count: {}", super.toString (), armyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultCountryPacket ()
  {
    super (null, null);

    this.armyCount = 0;
  }
}
