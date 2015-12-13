package com.forerunnergames.peril.core.model.map.continent;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.io.AbstractMapGraphDataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.graph.GraphModel;

public final class ContinentMapGraphDataLoader extends AbstractMapGraphDataLoader <Continent, ContinentMapGraphModel>
{
  private final CountryMapGraphModel countryMapGraphModel;

  public ContinentMapGraphDataLoader (final StreamParserFactory streamParserFactory,
                                      final ContinentFactory data,
                                      final CountryMapGraphModel countryMapGraphModel)
  {
    super (streamParserFactory, data.getContinents ());

    Arguments.checkIsNotNull (countryMapGraphModel, "countryMapGraphModel");

    this.countryMapGraphModel = countryMapGraphModel;
  }

  @Override
  protected ContinentMapGraphModel createGraphModel (final GraphModel <Continent> internalGraphModel)
  {
    return new ContinentMapGraphModel (internalGraphModel, countryMapGraphModel);
  }
}
