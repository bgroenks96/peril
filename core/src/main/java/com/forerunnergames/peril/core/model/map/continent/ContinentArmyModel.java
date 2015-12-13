package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.tools.common.id.Id;

public interface ContinentArmyModel
{
  int getArmyCountFor (final Id continentId);

  boolean armyCountIsAtLeast (final int minArmyCount, final Id continentId);
}
