package com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.screen;

import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;

// @formatter:off
public final class ScreenCoordinateToContinentNameConverter
                extends AbstractScreenCoordinateToTerritoryNameConverter <ContinentName>

{
  public ScreenCoordinateToContinentNameConverter (
                  final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                  final PlayMapCoordinateToTerritoryNameConverter <ContinentName> playMapCoordinateToContinentNameConverter)
  {
    super (screenToPlayMapCoordinateConverter, playMapCoordinateToContinentNameConverter);
  }
}
