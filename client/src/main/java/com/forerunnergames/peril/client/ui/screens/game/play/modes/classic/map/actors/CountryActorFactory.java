package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders.CountryImageLoader;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountryImageLoader countryImageLoader;
  private final CountryImageDataRepository countryImageDataRepository;

  public CountryActorFactory (final CountryImageLoader countryImageLoader,
                              final CountryImageDataRepository countryImageDataRepository)
  {
    Arguments.checkIsNotNull (countryImageLoader, "countryImagesRepository");
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");

    this.countryImageLoader = countryImageLoader;
    this.countryImageDataRepository = countryImageDataRepository;
  }

  public CountryActor create (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new CountryActor (countryImageLoader.getAllPrimary (countryName),
            countryImageLoader.getAllSecondary (countryName), countryImageData,
            CountryArmyTextActorFactory.create (countryImageData));
  }
}
