package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteColorOrderFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteData;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;

public final class PlayMapActorFactory
{
  public static PlayMapActor create (final PlayMapInputDetection playMapInputDetection)
  {
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");

    final CountrySpriteDataRepository countrySpriteDataRepository = CountrySpriteDataRepositoryFactory.create ();
    final CountryActorFactory countryActorFactory = new CountryActorFactory (CountrySpriteColorOrderFactory.create ());

    final ImmutableMap.Builder <CountryName, CountryActor> countryNamesToActorsBuilder = ImmutableMap.builder ();

    CountrySpriteData countrySpriteData;
    CountryActor countryActor;

    for (final CountryName countryName : countrySpriteDataRepository.getCountryNames ())
    {
      countrySpriteData = countrySpriteDataRepository.get (countryName);
      countryActor = countryActorFactory.create (countrySpriteData);

      countryNamesToActorsBuilder.put (countryName, countryActor);
    }

    return new PlayMapActor (countryNamesToActorsBuilder.build (), playMapInputDetection);
  }

  private PlayMapActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
