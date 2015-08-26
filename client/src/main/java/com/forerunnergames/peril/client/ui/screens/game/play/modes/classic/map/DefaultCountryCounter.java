package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io.loaders.CountryNamesDataLoader;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.map.PlayMapLoadingException;
import com.forerunnergames.peril.common.map.io.MapDataPathParser;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParserException;

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

    if (mapMetadata.equals (MapMetadata.NULL_MAP_METADATA)) return 0;

    try
    {
      return loader.load (pathParser.parseCountriesFileNamePath (mapMetadata)).size ();
    }
    catch (final StreamParserException e)
    {
      throw new PlayMapLoadingException (e);
    }
  }
}
