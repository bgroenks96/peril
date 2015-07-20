package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageDataRepositoryFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetectionFactory;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableMap;

import net.engio.mbassy.bus.MBassador;

public final class PlayMapActorFactory
{
  public static PlayMapActor create (final ScreenSize screenSize,
                                     final MouseInput mouseInput,
                                     final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    final PlayMapInputDetection playMapInputDetection = PlayMapInputDetectionFactory.create (screenSize);
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
            new HoveredTerritoryTextActor (playMapInputDetection, mouseInput), eventBus);
  }

  private PlayMapActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
