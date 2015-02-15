package com.forerunnergames.peril.core.shared.net.packets.defaults;

import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.net.packets.CountryPacket;

public final class DefaultCountryPacket implements CountryPacket
{
  private final CountryName name;
  private final int armyCount;

  public DefaultCountryPacket (final Country country)
  {
    name = country.getCountryName ();
    armyCount = country.getArmyCount ();
  }

  @Override
  public CountryName getCountryName ()
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
    return armyCount >= n;
  }
}
