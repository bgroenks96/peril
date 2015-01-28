package com.forerunnergames.peril.client.ui.screens.game.play.map.data;

import com.forerunnergames.peril.client.settings.AssetPaths;
import com.forerunnergames.peril.client.ui.screens.game.play.map.loaders.CountrySpriteColorOrderLoader;
import com.forerunnergames.tools.common.Classes;

public final class CountrySpriteColorOrderFactory
{
  private static final CountrySpriteColorOrderLoader COUNTRY_SPRITE_COLOR_ORDER_LOADER = new CountrySpriteColorOrderLoader ();

  public static CountrySpriteColorOrder create ()
  {
    return new CountrySpriteColorOrder (
                    COUNTRY_SPRITE_COLOR_ORDER_LOADER.load (AssetPaths.PLAY_MAP_COUNTRY_SPRITE_COLOR_ORDER_FILENAME));
  }

  private CountrySpriteColorOrderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
