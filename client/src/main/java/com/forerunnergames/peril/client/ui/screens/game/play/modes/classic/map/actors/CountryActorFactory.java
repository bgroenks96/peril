package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImagesRepository;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountryImageDataRepository countryImageDataRepository;
  private final CountryImagesRepository countryImagesRepository;

  public CountryActorFactory (final CountryImageDataRepository countryImageDataRepository,
                              final CountryImagesRepository countryImagesRepository)
  {
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");
    Arguments.checkIsNotNull (countryImagesRepository, "countryImagesRepository");

    this.countryImageDataRepository = countryImageDataRepository;
    this.countryImagesRepository = countryImagesRepository;
  }

  public CountryActor create (final String countryName, final BitmapFont countryArmyTextFont)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countryArmyTextFont, "countryArmyTextFont");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new DefaultCountryActor (countryImagesRepository.getPrimary (countryName),
            countryImagesRepository.getSecondary (countryName), countryImageData,
            CountryArmyTextActorFactory.create (countryImageData, countryArmyTextFont));
  }
}
