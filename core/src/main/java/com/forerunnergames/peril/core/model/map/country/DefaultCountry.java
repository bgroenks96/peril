package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultCountry extends AbstractAsset implements Country
{
  private int armyCount;

  public DefaultCountry (final String name, final Id id, final int armyCount)
  {
    super (name, id);

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
  public void addArmies (final int armyCount)
  {
    this.armyCount += armyCount;
  }

  @Override
  public void removeArmies (final int armyCount)
  {
    Arguments.checkIsNotNegative (armyCount, "armyCount");
    Arguments.checkUpperInclusiveBound (armyCount, this.armyCount, "armyCount");

    this.armyCount -= armyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Army Count: {}", super.toString (), armyCount);
  }

  @RequiredForNetworkSerialization
  private DefaultCountry ()
  {
    armyCount = 0;
  }
}
