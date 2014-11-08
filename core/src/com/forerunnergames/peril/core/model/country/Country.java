package com.forerunnergames.peril.core.model.country;

import com.forerunnergames.peril.core.model.territory.Territory;

public interface Country extends Territory
{
  public int getArmyCount();
  public boolean hasAnyArmies();
  public boolean hasAtLeastNArmies (int n);
}