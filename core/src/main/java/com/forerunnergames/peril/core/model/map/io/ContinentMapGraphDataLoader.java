package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.continent.Continent;

import com.google.common.collect.ImmutableSet;

public final class ContinentMapGraphDataLoader extends AbstractMapGraphDataLoader <Continent>
{
  public ContinentMapGraphDataLoader (final StreamParserFactory streamParserFactory,
                                      final ImmutableSet <Continent> data)
  {
    super (streamParserFactory, data);
  }
}
