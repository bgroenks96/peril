package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.MapResourcesPathParser;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

public final class CountryImageDataRepositoryFactory
{
  private final DataLoader <CountryName, CountryImageData> countryImageDataLoader;

  public CountryImageDataRepositoryFactory (final DataLoader <CountryName, CountryImageData> countryImageDataLoader)
  {
    Arguments.checkIsNotNull (countryImageDataLoader, "countryImageDataLoader");

    this.countryImageDataLoader = countryImageDataLoader;
  }

  public CountryImageDataRepository create (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    final MapResourcesPathParser mapResourcesPathParser = new AbsoluteMapResourcesPathParser (mapMetadata.getMode ());

    return new CountryImageDataRepository (
            countryImageDataLoader.load (mapResourcesPathParser.parseCountryImageDataFileNamePath (mapMetadata)));
  }
}
