package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.MapResourcesPathParser;
import com.forerunnergames.peril.common.io.BiMapDataLoader;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultCountryImageDataRepositoryFactory implements CountryImageDataRepositoryFactory
{
  private final BiMapDataLoader <String, CountryImageData> countryImageDataLoader;

  public DefaultCountryImageDataRepositoryFactory (final BiMapDataLoader <String, CountryImageData> countryImageDataLoader)
  {
    Arguments.checkIsNotNull (countryImageDataLoader, "countryImageDataLoader");

    this.countryImageDataLoader = countryImageDataLoader;
  }

  @Override
  public CountryImageDataRepository create (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    final MapResourcesPathParser mapResourcesPathParser = new AbsoluteMapResourcesPathParser (mapMetadata.getMode ());

    return new DefaultCountryImageDataRepository (
            countryImageDataLoader.load (mapResourcesPathParser.parseCountryImageDataFileNamePath (mapMetadata)));
  }
}
