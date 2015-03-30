package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.forerunnergames.peril.client.ui.screens.game.play.map.sprites.CountrySprites;
import com.forerunnergames.peril.client.ui.screens.game.play.map.data.CountrySpriteDataRepository;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;

public final class CountryActorFactory
{
  private final CountrySprites countrySprites;
  private final CountrySpriteDataRepository countrySpriteDataRepository;

  public CountryActorFactory (final CountrySprites countrySprites,
                              final CountrySpriteDataRepository countrySpriteDataRepository)
  {
    Arguments.checkIsNotNull (countrySprites, "countrySprites");
    Arguments.checkIsNotNull (countrySpriteDataRepository, "countrySpriteDataRepository");

    this.countrySprites = countrySprites;
    this.countrySpriteDataRepository = countrySpriteDataRepository;
  }

  public CountryActor create (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return new CountryActor (countrySprites.get (countryName), countrySpriteDataRepository.get (countryName));
  }
}
