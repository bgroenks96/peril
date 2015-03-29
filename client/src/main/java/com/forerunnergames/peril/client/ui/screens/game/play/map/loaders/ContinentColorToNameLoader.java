package com.forerunnergames.peril.client.ui.screens.game.play.map.loaders;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.ContinentColor;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;
import com.forerunnergames.tools.common.Arguments;

public final class ContinentColorToNameLoader extends
        AbstractTerritoryColorToNameLoader <ContinentColor, ContinentName>
{
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
