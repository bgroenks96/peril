package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.tools.common.Arguments;

public final class CountryImageDataRepositoryFactory
{
  private final DataLoader <CountryName, CountryImageData> countryImageDataLoader;

  public CountryImageDataRepositoryFactory (final DataLoader <CountryName, CountryImageData> countryImageDataLoader)
  {
    Arguments.checkIsNotNull (countryImageDataLoader, "countryImageDataLoader");

    this.countryImageDataLoader = countryImageDataLoader;
  }

  public CountryImageDataRepository create ()
  {
    return new CountryImageDataRepository (
            countryImageDataLoader.load (ClassicPlayMapSettings.COUNTRY_IMAGE_DATA_FILENAME));
  }
}
