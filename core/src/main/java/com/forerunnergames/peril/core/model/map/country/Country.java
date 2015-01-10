package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.core.model.map.territory.Territory;

public interface Country extends Territory
{
  public int getArmyCount ();

  public boolean hasAnyArmies ();

  public boolean hasAtLeastNArmies (int n);
}
