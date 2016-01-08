package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;

public interface PlayMapModelFactory
{
  PlayMapModel create (final CountryMapGraphModel countryMapGraphModel,
                       final ContinentMapGraphModel continentMapGraphModel);

}
