package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageRepository;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;

public final class PlayMapActorFactory
{
  public static PlayMapActor create (final CountryImageRepository countryImageRepository,
                                     final CountryImageDataRepository countryImageDataRepository,
                                     final PlayMapInputDetection playMapInputDetection)
  {
    Arguments.checkIsNotNull (countryImageRepository, "countrySprites");
    Arguments.checkIsNotNull (countryImageDataRepository, "countrySpriteDataRepository");
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");

    final CountryActorFactory countryActorFactory = new CountryActorFactory (countryImageRepository, countryImageDataRepository);

    final ImmutableMap.Builder <CountryName, CountryActor> countryNamesToActorsBuilder = ImmutableMap.builder ();

    for (final CountryName countryName : countryImageDataRepository.getCountryNames ())
    {
      countryNamesToActorsBuilder.put (countryName, countryActorFactory.create (countryName));
    }

    return new PlayMapActor (countryNamesToActorsBuilder.build (), playMapInputDetection);
  }

  private PlayMapActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
