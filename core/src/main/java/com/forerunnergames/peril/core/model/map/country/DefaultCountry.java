package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultCountry extends AbstractAsset implements Country
{
  private final int armyCount;
  private final CountryName countryName;

  public DefaultCountry (final String name, final Id id, final int armyCount)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    this.armyCount = armyCount;
    this.countryName = new CountryName (name);
  }

  @Override
  public CountryName getCountryName ()
  {
    return countryName;
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
}
