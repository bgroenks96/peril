package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.input;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.country.CountryName;

public final class InputCoordinateToCountryNameConverter extends
        AbstractInputCoordinateToTerritoryNameConverter <CountryName>

{
  public InputCoordinateToCountryNameConverter (final InputToScreenCoordinateConverter inputToScreenCoordinateConverter,
                                                final ScreenCoordinateToTerritoryNameConverter <CountryName> screenCoordinateToCountryNameConverter)
  {
    super (inputToScreenCoordinateConverter, screenCoordinateToCountryNameConverter);
  }
}
