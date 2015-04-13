package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageRepository;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountryImageRepository countryImageRepository;
  private final CountryImageDataRepository countryImageDataRepository;

  public CountryActorFactory (final CountryImageRepository countryImageRepository,
                              final CountryImageDataRepository countryImageDataRepository)
  {
    Arguments.checkIsNotNull (countryImageRepository, "countryImageRepository");
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");

    this.countryImageRepository = countryImageRepository;
    this.countryImageDataRepository = countryImageDataRepository;
  }

  public CountryActor create (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new CountryActor (countryImageRepository.getAll (countryName), countryImageData,
            CountryArmyTextActorFactory.create (countryImageData));
  }
}
