package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders.CountryImageDataLoader;
import com.forerunnergames.tools.common.Classes;

public final class CountryImageDataRepositoryFactory
{
  private static final CountryImageDataLoader COUNTRY_IMAGE_DATA_LOADER = new CountryImageDataLoader ();

  public static CountryImageDataRepository create ()
  {
    return new CountryImageDataRepository (
            COUNTRY_IMAGE_DATA_LOADER.load (ClassicPlayMapSettings.COUNTRY_IMAGE_DATA_FILENAME));
  }

  private CountryImageDataRepositoryFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
