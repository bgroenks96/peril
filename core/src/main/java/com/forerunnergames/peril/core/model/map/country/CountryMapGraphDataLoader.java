package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.io.AbstractMapGraphDataLoader;
import com.forerunnergames.tools.common.graph.GraphModel;

public final class CountryMapGraphDataLoader extends AbstractMapGraphDataLoader <Country, CountryMapGraphModel>
{
  public CountryMapGraphDataLoader (final StreamParserFactory streamParserFactory, final CountryFactory factory)
  {
    super (streamParserFactory, factory.getCountries ());
  }

  @Override
  protected CountryMapGraphModel finalizeData (final GraphModel <Country> internalGraphModel)
  {
    return new CountryMapGraphModel (internalGraphModel);
  }
}
