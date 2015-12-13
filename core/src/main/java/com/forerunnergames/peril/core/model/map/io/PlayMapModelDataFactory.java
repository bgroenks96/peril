package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;

public interface PlayMapModelDataFactory
{
  CountryFactory createCountries (final MapMetadata mapMetadata);

  ContinentFactory createContinents (final MapMetadata mapMetadata, final CountryIdResolver countryIdResolver);

  CountryMapGraphModel createCountryMapGraphModel (final MapMetadata mapMetadata, final CountryFactory countryFactory);

  ContinentMapGraphModel createContinentMapGraphModel (final MapMetadata mapMetadata,
                                                       final ContinentFactory continentFactory,
                                                       final CountryMapGraphModel countryMapGraphModel);
}
