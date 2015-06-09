package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.core.model.map.territory.Territory;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public interface Continent extends Territory
{
  ContinentName getContinentName ();

  ImmutableSet <Id> getCountryIds ();

  boolean hasCountry (final Id country);

  int getReinforcementBonus ();
}
