package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.country.Country;

import com.google.common.collect.ImmutableSet;

public final class CountryMapGraphDataLoader extends AbstractMapGraphDataLoader <Country>
{
  public CountryMapGraphDataLoader (final StreamParserFactory streamParserFactory,
                                    final ImmutableSet <Country> countries)
  {
    super (streamParserFactory, countries);
  }
}
