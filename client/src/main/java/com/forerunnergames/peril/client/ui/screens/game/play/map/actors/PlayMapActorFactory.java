package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.map.sprites.CountrySprites;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;

public final class PlayMapActorFactory
{
  public static PlayMapActor create (final CountrySprites countrySprites,
                                     final CountrySpriteDataRepository countrySpriteDataRepository,
                                     final PlayMapInputDetection playMapInputDetection)
  {
    Arguments.checkIsNotNull (countrySprites, "countrySprites");
    Arguments.checkIsNotNull (countrySpriteDataRepository, "countrySpriteDataRepository");
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");

    final CountryActorFactory countryActorFactory = new CountryActorFactory (countrySprites, countrySpriteDataRepository);

    final ImmutableMap.Builder <CountryName, CountryActor> countryNamesToActorsBuilder = ImmutableMap.builder ();

    for (final CountryName countryName : countrySpriteDataRepository.getCountryNames ())
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
