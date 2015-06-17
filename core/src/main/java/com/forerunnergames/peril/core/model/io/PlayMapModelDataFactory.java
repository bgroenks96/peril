package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.settings.AssetSettings;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class PlayMapModelDataFactory
{
  public static ImmutableSet <Country> createCountries ()
  {
    return ImmutableSet.copyOf (new CountryModelDataLoader ().load (AssetSettings.COUNTRY_DATA_FILENAME).values ());
  }

  public static ImmutableSet <Continent> createContinents (final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    final DataLoader <Id, Continent> continentLoader = new ContinentModelDataLoader (countryIdResolver);

    return ImmutableSet.copyOf (continentLoader.load (AssetSettings.CONTINENT_DATA_FILENAME).values ());
  }

  private PlayMapModelDataFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
