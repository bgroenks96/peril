package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.screen;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetocoordinate.ScreenToPlayMapCoordinateConverter;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.converters.coordinatetoname.playmap.PlayMapCoordinateToTerritoryNameConverter;
import com.forerunnergames.peril.core.model.map.country.CountryName;

// @formatter:off
public final class ScreenCoordinateToCountryNameConverter
                extends AbstractScreenCoordinateToTerritoryNameConverter <CountryName>

{
  public ScreenCoordinateToCountryNameConverter (
                  final ScreenToPlayMapCoordinateConverter screenToPlayMapCoordinateConverter,
                  final PlayMapCoordinateToTerritoryNameConverter <CountryName> playMapCoordinateToCountryNameConverter)
  {
    super (screenToPlayMapCoordinateConverter, playMapCoordinateToCountryNameConverter);
  }
}
