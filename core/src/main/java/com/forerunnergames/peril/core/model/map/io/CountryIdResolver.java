package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.tools.common.id.Id;

public interface CountryIdResolver
{
  boolean has (final String countryName);
  Id getIdOf (final String countryName);
}
