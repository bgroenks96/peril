package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCountry extends AbstractAsset implements Country
{
  private final CountryName name;
  private final int armyCount;

  public DefaultCountry (final String name, final Id id, final int armyCount)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    this.armyCount = armyCount;
    this.name = new CountryName (name);
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
    Arguments.checkIsNotNegative (n, "n");

    return armyCount >= n;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | %2$s | Army Count: %3$s", super.toString (), name, armyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultCountry ()
  {
    name = null;
    armyCount = 0;
  }
}
