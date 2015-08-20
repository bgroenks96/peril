package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.core.model.map.territory.Territory;

public interface Country extends Territory
{
  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (int n);
}
