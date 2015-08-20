package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.io;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;

public final class ContinentColorToNameLoader extends AbstractTerritoryColorToNameLoader <ContinentColor>
{
  public ContinentColorToNameLoader (final StreamParserFactory streamParserFactory)
  {
    super (streamParserFactory);
  }

  @Override
  protected ContinentColor createTerritoryColor (final int colorComponentValue)
  {
    Arguments.checkIsNotNull (colorComponentValue, "colorComponentValue");

    return new ContinentColor (colorComponentValue);
  }
}
