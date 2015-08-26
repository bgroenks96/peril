package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryNamesDataLoader;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.pathparsers.AbsoluteMapResourcesPathParser;
import com.forerunnergames.peril.common.io.ExternalStreamParserFactory;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CountryCounterFactory
{
  public static CountryCounter create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultCountryCounter (new CountryNamesDataLoader (new ExternalStreamParserFactory ()),
            new AbsoluteMapResourcesPathParser (gameMode));
  }

  private CountryCounterFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
