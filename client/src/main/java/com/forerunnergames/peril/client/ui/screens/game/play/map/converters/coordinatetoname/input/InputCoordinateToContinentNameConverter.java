package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.input;

import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocoordinate.InputToScreenCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.screen.ScreenCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;

public final class InputCoordinateToContinentNameConverter extends
                AbstractInputCoordinateToTerritoryNameConverter <ContinentName>

{
  public InputCoordinateToContinentNameConverter (final InputToScreenCoordinateConverter inputToScreenCoordinateConverter,
                                                  final ScreenCoordinateToTerritoryNameConverter <ContinentName> screenCoordinateToContinentNameConverter)
  {
    super (inputToScreenCoordinateConverter, screenCoordinateToContinentNameConverter);
  }
}
