package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocolor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.colors.CountryColor;
import com.forerunnergames.tools.common.color.RgbaColor;

// @formatter:off
public final class PlayMapCoordinateToCountryColorConverter
                extends AbstractPlayMapCoordinateToTerritoryColorConverter <CountryColor>
{
  public PlayMapCoordinateToCountryColorConverter (final PlayMapCoordinateToRgbaColorConverter converter)
  {
    super (converter);
  }

  @Override
  protected CountryColor createTerritoryColor (final RgbaColor color)
  {
    return new CountryColor (color.getRed ());
  }
}
