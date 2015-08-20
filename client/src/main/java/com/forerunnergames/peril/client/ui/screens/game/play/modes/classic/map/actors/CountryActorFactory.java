package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.CountryImagesLoader;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountryImagesLoader countryImagesLoader;
  private final CountryImageDataRepository countryImageDataRepository;

  public CountryActorFactory (final CountryImagesLoader countryImagesLoader,
                              final CountryImageDataRepository countryImageDataRepository)
  {
    Arguments.checkIsNotNull (countryImagesLoader, "countryImagesLoader");
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");

    this.countryImagesLoader = countryImagesLoader;
    this.countryImageDataRepository = countryImageDataRepository;
  }

  public CountryActor create (final String countryName, final BitmapFont countryArmyTextFont)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countryArmyTextFont, "countryArmyTextFont");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new DefaultCountryActor (countryImagesLoader.getAllPrimary (countryName),
            countryImagesLoader.getAllSecondary (countryName), countryImageData,
            CountryArmyTextActorFactory.create (countryImageData, countryArmyTextFont));
  }
}
