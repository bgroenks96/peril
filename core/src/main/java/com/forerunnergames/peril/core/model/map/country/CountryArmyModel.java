package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

public interface CountryArmyModel
{
  Result <Reason> requestToAddArmiesToCountry (final Id countryId, final int armyCount);

  Result <Reason> requestToRemoveArmiesFromCountry (final Id countryId, final int armyCount);

  int getArmyCountFor (final Id countryId);

  boolean armyCountIs (final int armyCount, final Id countryId);

  boolean armyCountIsAtLeast (final int minArmyCount, final Id countryId);
}
