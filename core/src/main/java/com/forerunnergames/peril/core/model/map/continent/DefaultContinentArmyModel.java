package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultContinentArmyModel implements ContinentArmyModel
{
  private final ContinentMapGraphModel continentMapGraphModel;

  public DefaultContinentArmyModel (final ContinentMapGraphModel continentMapGraphModel)
  {
    Arguments.checkIsNotNull (continentMapGraphModel, "continentMapGraphModel");

    this.continentMapGraphModel = continentMapGraphModel;
  }

  @Override
  public int getArmyCountFor (final Id continentId)
  {
    Arguments.checkIsNotNull (continentId, "continentId");
    Preconditions.checkIsTrue (continentMapGraphModel.existsContinentWith (continentId),
                               Strings.format ("No continent with id [{}] exists.", continentId));

    int totalArmyCount = 0;
    for (final CountryPacket country : continentMapGraphModel.getCountriesIn (continentId))
    {
      totalArmyCount += country.getArmyCount ();
    }
    return totalArmyCount;
  }

  @Override
  public boolean armyCountIsAtLeast (final int minArmyCount, final Id continentId)
  {
    Arguments.checkIsNotNegative (minArmyCount, "minArmyCount");
    Arguments.checkIsNotNull (continentId, "continentId");

    return getArmyCountFor (continentId) >= minArmyCount;
  }
}
