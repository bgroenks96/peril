package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import com.google.common.collect.ImmutableMap;

public final class PlayMapActorFactory
{
  public static PlayMapActor create (final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    final PlayMapInputDetection playMapInputDetection = PlayMapInputDetectionFactory.create ();
    final CountryImageRepository countryImageRepository = new CountryImageRepository ();
    final CountryImageDataRepository countryImageDataRepository = CountryImageDataRepositoryFactory.create ();

    final CountryActorFactory countryActorFactory = new CountryActorFactory (countryImageRepository,
            countryImageDataRepository);

    final ImmutableMap.Builder <CountryName, CountryActor> countryNamesToActorsBuilder = ImmutableMap.builder ();

    for (final CountryName countryName : countryImageDataRepository.getCountryNames ())
    {
      countryNamesToActorsBuilder.put (countryName, countryActorFactory.create (countryName));
    }

    return new PlayMapActor (countryNamesToActorsBuilder.build (), playMapInputDetection,
            new HoveredTerritoryTextActor (playMapInputDetection, mouseInput));
  }

  private PlayMapActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
