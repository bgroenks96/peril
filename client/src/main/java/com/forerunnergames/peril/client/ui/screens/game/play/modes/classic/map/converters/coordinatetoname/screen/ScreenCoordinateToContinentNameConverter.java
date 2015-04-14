package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.continent.ContinentName;

public final class ScreenCoordinateToContinentNameConverter extends
        AbstractScreenCoordinateToTerritoryNameConverter <ContinentName>

{
  public ScreenCoordinateToContinentNameConverter (final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                                                   final PlayMapCoordinateToTerritoryNameConverter <ContinentName> playMapCoordinateToContinentNameConverter)
  {
    super (screenToPlayMapCoordinateConverter, playMapCoordinateToContinentNameConverter);
  }
}
