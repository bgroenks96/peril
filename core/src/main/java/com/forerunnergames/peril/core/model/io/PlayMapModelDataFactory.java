package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.shared.settings.AssetSettings;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class PlayMapModelDataFactory
{
  public static ImmutableSet <Country> createCountries ()
  {
    return ImmutableSet.copyOf (new CountryModelDataLoader (new ExternalStreamParserFactory ())
            .load (AssetSettings.ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_COUNTRY_DATA_DIRECTORY
                    + AssetSettings.COUNTRY_DATA_FILENAME)
            .values ());
  }

  public static ImmutableSet <Continent> createContinents (final CountryIdResolver countryIdResolver)
  {
    Arguments.checkIsNotNull (countryIdResolver, "countryIdResolver");

    final DataLoader <Id, Continent> continentLoader = new ContinentModelDataLoader (new ExternalStreamParserFactory (),
            countryIdResolver);

    return ImmutableSet.copyOf (continentLoader
            .load (AssetSettings.ABSOLUTE_EXTERNAL_CLASSIC_MODE_CLASSIC_MAP_CONTINENT_DATA_DIRECTORY
                    + AssetSettings.CONTINENT_DATA_FILENAME)
            .values ());
  }

  private PlayMapModelDataFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
