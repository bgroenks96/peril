package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;
import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;

public final class CountryColorToNameLoader extends AbstractTerritoryColorToNameLoader <CountryColor>
{
  public CountryColorToNameLoader (final StreamParserFactory streamParserFactory)
  {
    super (streamParserFactory);
  }

  @Override
  protected CountryColor createTerritoryColor (final int colorComponentValue)
  {
    Arguments.checkIsNotNull (colorComponentValue, "colorComponentValue");

    return new CountryColor (colorComponentValue);
  }
}