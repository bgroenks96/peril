package com.forerunnergames.peril.core.model.map.io;

import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.map.MapType;
import com.forerunnergames.peril.common.map.io.DefaultMapMetadataFinder;
import com.forerunnergames.peril.common.map.io.MapMetadataFinder;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CoreMapMetadataFinderFactory
{
  public static MapMetadataFinder create (final GameMode gameMode)
  {
    Arguments.checkIsNotNull (gameMode, "gameMode");

    return new DefaultMapMetadataFinder (gameMode,
            new CoreMapMetadataLoaderFactory (gameMode).create (MapType.STOCK, MapType.CUSTOM).load ());
  }

  private CoreMapMetadataFinderFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
