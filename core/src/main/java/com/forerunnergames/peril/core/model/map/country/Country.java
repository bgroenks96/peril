package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.core.model.map.territory.Territory;

interface Country extends Territory
{
  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (final int n);

  void addArmies (final int armyCount);

  void removeArmies (final int armyCount);
}
