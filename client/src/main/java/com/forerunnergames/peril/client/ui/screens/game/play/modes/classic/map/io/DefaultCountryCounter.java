package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.CountryCounter;
import com.forerunnergames.peril.core.shared.map.MapMetadata;
import com.forerunnergames.peril.core.shared.map.io.MapDataPathParser;
import com.forerunnergames.tools.common.Arguments;

public final class DefaultCountryCounter implements CountryCounter
{
  private final CountryNamesDataLoader loader;
  private final MapDataPathParser pathParser;

  public DefaultCountryCounter (final CountryNamesDataLoader loader, final MapDataPathParser pathParser)
  {
    Arguments.checkIsNotNull (loader, "loader");
    Arguments.checkIsNotNull (pathParser, "pathParser");

    this.loader = loader;
    this.pathParser = pathParser;
  }

  @Override
  public int count (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return loader.load (pathParser.parseCountriesFileNamePath (mapMetadata)).size ();
  }
}
