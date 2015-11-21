package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.tools.common.graph.GraphModel;

import com.google.common.collect.ImmutableSet;

public interface PlayMapModelDataFactory
{
  ImmutableSet <Country> createCountries (final MapMetadata mapMetadata);

  ImmutableSet <Continent> createContinents (final MapMetadata mapMetadata, final CountryIdResolver countryIdResolver);

  GraphModel <Country> createCountryGraph (final MapMetadata mapMetadata, final ImmutableSet <Country> countries);

  GraphModel <Continent> createContinentGraph (final MapMetadata mapMetadata,
                                               final ImmutableSet <Continent> continents);
}
