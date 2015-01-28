package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocolor;

import com.forerunnergames.peril.client.ui.screens.game.play.map.colors.ContinentColor;
import com.forerunnergames.tools.common.color.RgbaColor;

// @formatter:off
public final class PlayMapCoordinateToContinentColorConverter
                extends AbstractPlayMapCoordinateToTerritoryColorConverter <ContinentColor>
{
  public PlayMapCoordinateToContinentColorConverter (final PlayMapCoordinateToRgbaColorConverter converter)
  {
    super (converter);
  }

  @Override
  protected ContinentColor createTerritoryColor (final RgbaColor color)
  {
    return new ContinentColor (color.getGreen ());
  }
}
