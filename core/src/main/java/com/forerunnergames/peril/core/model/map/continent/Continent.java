package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.core.model.map.territory.Territory;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

interface Continent extends Territory
{
  ImmutableSet <Id> getCountryIds ();

  int getCountryCount ();

  boolean hasCountry (final Id country);

  int getReinforcementBonus ();
}
