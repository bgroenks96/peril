package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImagesRepository;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountryImageDataRepository countryImageDataRepository;
  private final CountryImagesRepository countryImagesRepository;
  private final Vector2 playMapReferenceSize;

  public CountryActorFactory (final CountryImageDataRepository countryImageDataRepository,
                              final CountryImagesRepository countryImagesRepository,
                              final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");
    Arguments.checkIsNotNull (countryImagesRepository, "countryImagesRepository");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    this.countryImageDataRepository = countryImageDataRepository;
    this.countryImagesRepository = countryImagesRepository;
    this.playMapReferenceSize = playMapReferenceSize;
  }

  public CountryActor create (final String countryName, final BitmapFont countryArmyTextFont)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countryArmyTextFont, "countryArmyTextFont");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new DefaultCountryActor (countryImagesRepository.getPrimary (countryName),
            countryImagesRepository.getSecondary (countryName), countryImageData,
            CountryArmyTextActorFactory.create (countryImageData, countryArmyTextFont, playMapReferenceSize),
            playMapReferenceSize);
  }
}
