package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.ContinentColor;
import com.forerunnergames.peril.core.shared.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.tools.common.Arguments;

public final class ContinentColorToNameLoader extends
        AbstractTerritoryColorToNameLoader <ContinentColor, ContinentName>
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

  @Override
  protected ContinentName createTerritoryName (final String nameValue)
  {
    Arguments.checkIsNotNull (nameValue, "nameValue");

    return new ContinentName (nameValue);
  }
}
