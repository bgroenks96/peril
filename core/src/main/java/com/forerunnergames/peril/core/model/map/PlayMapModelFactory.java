package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;

public interface PlayMapModelFactory
{
  PlayMapModel create (final CountryFactory countryFactory,
                       final CountryMapGraphModel countryMapGraphModel,
                       final ContinentFactory continentFactory,
                       final ContinentMapGraphModel continentMapGraphModel);

}
