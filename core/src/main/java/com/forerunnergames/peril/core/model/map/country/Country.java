package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.core.model.map.territory.Territory;

public interface Country extends Territory
{
  CountryName getCountryName ();

  int getArmyCount ();

  boolean hasAnyArmies ();

  boolean hasAtLeastNArmies (int n);
}
