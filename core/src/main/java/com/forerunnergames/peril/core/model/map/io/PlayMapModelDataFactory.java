package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.shared.map.MapMetadata;

import com.google.common.collect.ImmutableSet;

public interface PlayMapModelDataFactory
{
  ImmutableSet <Country> createCountries (final MapMetadata mapMetadata);

  ImmutableSet <Continent> createContinents (final MapMetadata mapMetadata, final CountryIdResolver countryIdResolver);
}
