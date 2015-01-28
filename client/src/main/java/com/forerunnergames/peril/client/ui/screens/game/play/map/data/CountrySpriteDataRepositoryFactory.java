package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

import com.forerunnergames.peril.client.settings.AssetPaths;
import com.forerunnergames.peril.client.ui.screens.game.play.map.loaders.CountrySpriteDataLoader;
import com.forerunnergames.tools.common.Classes;

// @formatter:off
public final class CountrySpriteDataRepositoryFactory
{
  private static final CountrySpriteDataLoader COUNTRY_SPRITE_DATA_LOADER = new CountrySpriteDataLoader ();

  public static CountrySpriteDataRepository create ()
  {
    return new CountrySpriteDataRepository (COUNTRY_SPRITE_DATA_LOADER.load (AssetPaths.PLAY_MAP_COUNTRY_SPRITE_DATA_FILENAME));
  }

  private CountrySpriteDataRepositoryFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
